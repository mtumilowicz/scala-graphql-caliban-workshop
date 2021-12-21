package app.infrastructure.config.customer

import app.domain.customer._
import app.domain.customerdetails.CustomerDetailsService
import app.domain.id.IdService
import app.domain.{ApiRepositoryEnv, InternalServiceEnv}
import app.infrastructure.customer.CustomerInMemoryRepository
import zio.{URLayer, ZIO}

object CustomerConfig {

  val inMemoryRepository: URLayer[Any, CustomerRepositoryEnv] =
    CustomerInMemoryRepository.live

  val service: URLayer[InternalServiceEnv with ApiRepositoryEnv, CustomerServiceEnv] = {
    for {
      idService <- ZIO.service[IdService]
      detailsService <- ZIO.service[CustomerDetailsService]
      repository <- ZIO.service[CustomerRepository]
    } yield CustomerService(idService, detailsService, repository)
  }.toLayer

}
