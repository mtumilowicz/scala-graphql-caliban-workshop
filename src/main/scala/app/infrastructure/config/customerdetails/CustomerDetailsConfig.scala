package app.infrastructure.config.customerdetails

import app.domain.customerdetails.{CustomerDetailsRepository, CustomerDetailsRepositoryEnv, CustomerDetailsService, CustomerDetailsServiceEnv}
import app.infrastructure.customer.CustomerDetailsInMemoryRepository
import zio.{URLayer, ZIO}

object CustomerDetailsConfig {

  val inMemoryRepository: URLayer[Any, CustomerDetailsRepositoryEnv] =
    CustomerDetailsInMemoryRepository.live

  val service: URLayer[CustomerDetailsRepositoryEnv, CustomerDetailsServiceEnv] = {
    for {
      repository <- ZIO.service[CustomerDetailsRepository]
    } yield CustomerDetailsService(repository)
  }.toLayer

}
