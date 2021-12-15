package app.gateway.customer

import app.domain.customer._
import app.gateway.customer.in.NewCustomerApiInput
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import zio.RIO

case class CustomerGraphQlMutations private(create: NewCustomerApiInput => RIO[CustomerServiceEnv, CustomerApiOutput])

object CustomerGraphQlMutations {

  def apply(baseUrl: String): CustomerGraphQlMutations = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlMutations(input => CustomerServiceProxy.create(input.toDomain).map(CustomerApiOutput(rootUri, _)))
  }
}

