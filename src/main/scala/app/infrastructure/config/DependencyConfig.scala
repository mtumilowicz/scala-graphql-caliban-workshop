package app.infrastructure.config

import app.domain._
import app.gateway.GraphQlEnv
import app.infrastructure.config.customer.CustomerConfig
import app.infrastructure.config.customerdetails.CustomerDetailsConfig
import app.infrastructure.config.http.HttpConfig
import app.infrastructure.config.id.IdConfig
import zio.blocking.Blocking
import zio.clock.Clock
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
      IdConfig.deterministicRepository ++ CustomerDetailsConfig.inMemoryRepository ++ ZLayer.identity

    val internalService: ZLayer[InternalRepository, Throwable, InternalService] =
      IdConfig.service ++ CustomerDetailsConfig.service ++ ZLayer.identity

    val apiRepository: ZLayer[InternalService, Throwable, ApiRepository] =
      CustomerConfig.inMemoryRepository ++ IdConfig.service ++ ZLayer.identity

    val apiService: ZLayer[ApiRepository, Throwable, ApiService] =
      CustomerConfig.service ++ ZLayer.identity

    val appLayer: ZLayer[Blocking, Throwable, AppEnv] =
      core >>> gateway >>> internalRepository >>> internalService >>> apiRepository >>> apiService
  }

  object inMemory {
    private val internalRepository: URLayer[Any, InternalRepositoryEnv] = IdConfig.deterministicRepository ++ CustomerDetailsConfig.inMemoryRepository
    private val internalService: URLayer[InternalRepositoryEnv, InternalServiceEnv] = IdConfig.service ++ CustomerDetailsConfig.service
    private val apiRepository: URLayer[Any, ApiRepositoryEnv] = CustomerConfig.inMemoryRepository
    private val apiService: URLayer[InternalServiceEnv with ApiRepositoryEnv, ApiServiceEnv] = CustomerConfig.service
    val appLayer: URLayer[Any, GraphQlEnv] = ((internalRepository >>> internalService) ++ apiRepository) >>> (Console.live ++ Clock.live ++ apiService)
  }
}