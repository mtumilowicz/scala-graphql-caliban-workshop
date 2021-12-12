package app.gateway.customer

import app.domain.{Customer, CustomerId, CustomerServiceEnv}
import app.infrastructure.config.customer.CustomerServiceProxy
import zio.URIO

case class CustomerGraphQlQueries private (findById: CustomerId => URIO[CustomerServiceEnv, Option[Customer]])

object CustomerGraphQlQueries {

  def apply(): CustomerGraphQlQueries =
    CustomerGraphQlQueries(id => CustomerServiceProxy.getById(id))
}
