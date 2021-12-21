package app.gateway.customer

import app.domain.customer._
import app.gateway.customer.in.NewCustomerApiInput
import app.gateway.customer.out.CreatedCustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import zio.RIO

case class CustomerGraphQlMutations private(create: NewCustomerApiInput => RIO[CustomerServiceEnv, CreatedCustomerApiOutput],
                                            deleteById: CustomerId => RIO[CustomerServiceEnv, CustomerId])

object CustomerGraphQlMutations {

  def apply(baseUrl: String): CustomerGraphQlMutations = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlMutations(
      input => CustomerServiceProxy.create(input.toDomain).map(CreatedCustomerApiOutput.fromDomain(rootUri, _)),
      id => CustomerServiceProxy.delete(id)
    )
  }
}

