package app.infrastructure.config.customer

import app.domain._
import app.infrastructure.customer.CustomerInMemoryRepository
import zio.blocking.Blocking
import zio.{URLayer, ZIO, ZLayer}

object CustomerConfig {

  val inMemoryRepository: URLayer[Any, CustomerRepositoryEnv] =
    CustomerInMemoryRepository.live

  val dbRepository: ZLayer[Blocking, Throwable, CustomerRepositoryEnv] =
    CustomerInMemoryRepository.live

  val service: URLayer[CustomerRepositoryEnv with IdServiceEnv, CustomerServiceEnv] = {
    for {
      service <- ZIO.service[IdService]
      repository <- ZIO.service[CustomerRepository]
    } yield CustomerService(service, repository)
  }.toLayer

}
