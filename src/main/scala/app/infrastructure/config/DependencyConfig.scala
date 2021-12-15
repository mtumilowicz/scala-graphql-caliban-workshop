package app.infrastructure.config

import app.domain._
import app.domain.customer._
import app.infrastructure.config.customer.CustomerConfig
import app.infrastructure.config.http.HttpConfig
import app.infrastructure.config.id.IdConfig
import zio.blocking.Blocking
import zio.console.Console
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger
import zio.{URLayer, ZLayer}

object DependencyConfig {

  type Core =
    AppConfigEnv with Logging with Blocking with Console

  type Gateway =
    Core with HttpConfigEnv

  type InternalRepository =
    Gateway with InternalRepositoryEnv

  type InternalService =
    InternalRepository with InternalServiceEnv

  type ApiRepository =
    InternalService with ApiRepositoryEnv

  type ApiService =
    ApiRepository with ApiServiceEnv

  type AppEnv = ApiService

  object live {

    val core: ZLayer[Blocking, Throwable, Core] =
      Blocking.any ++ AppConfig.live ++ Slf4jLogger.make((_, msg) => msg) ++ Console.live

    val gateway: ZLayer[Core, Throwable, Gateway] =
      HttpConfig.fromAppConfig ++ ZLayer.identity

    val internalRepository: ZLayer[Gateway, Throwable, InternalRepository] =
      IdConfig.deterministicRepository ++ ZLayer.identity

    val internalService: ZLayer[InternalRepository, Throwable, InternalService] =
      IdConfig.service ++ ZLayer.identity

    val apiRepository: ZLayer[InternalService, Throwable, ApiRepository] =
      CustomerConfig.dbRepository ++ IdConfig.service ++ ZLayer.identity

    val apiService: ZLayer[ApiRepository, Throwable, ApiService] =
      CustomerConfig.service ++ ZLayer.identity

    val appLayer: ZLayer[Blocking, Throwable, AppEnv] =
      core >>> gateway >>> internalRepository >>> internalService >>> apiRepository >>> apiService
  }

  object inMemory {
    private val internalRepository: URLayer[Any, IdProviderEnv] = IdConfig.deterministicRepository
    private val internalService: URLayer[IdProviderEnv, IdServiceEnv] = IdConfig.service
    private val apiRepository: URLayer[Any, CustomerRepositoryEnv] = CustomerConfig.inMemoryRepository
    private val apiService: URLayer[CustomerRepositoryEnv with IdServiceEnv, CustomerServiceEnv] = CustomerConfig.service
    val appLayer = internalRepository >>> internalService ++ apiRepository >>> apiService
  }
}