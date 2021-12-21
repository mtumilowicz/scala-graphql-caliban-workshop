package app.infrastructure.config.customer

import app.domain.customer._
import app.domain.customerdetails.{CustomerDetailsService, CustomerDetailsServiceEnv}
import app.infrastructure.customer.CustomerInMemoryRepository
import zio.{URLayer, ZIO}

object CustomerConfig {

  type CustomerEnv = CustomerRepositoryEnv with IdServiceEnv with CustomerDetailsServiceEnv

  val inMemoryRepository: URLayer[Any, CustomerRepositoryEnv] =
    CustomerInMemoryRepository.live

  val service: URLayer[CustomerEnv, CustomerServiceEnv] = {
    for {
      idService <- ZIO.service[IdService]
      detailsService <- ZIO.service[CustomerDetailsService]
      repository <- ZIO.service[CustomerRepository]
    } yield CustomerService(idService, detailsService, repository)
  }.toLayer

}
