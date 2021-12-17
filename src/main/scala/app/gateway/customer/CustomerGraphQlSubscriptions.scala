package app.gateway.customer

import app.domain.customer.CustomerServiceEnv
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import zio.stream.ZStream

case class CustomerGraphQlSubscriptions(createdCustomers: ZStream[CustomerServiceEnv, Throwable, CustomerApiOutput])

object CustomerGraphQlSubscriptions {

  def apply(baseUrl: String): CustomerGraphQlSubscriptions = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlSubscriptions(CustomerServiceProxy.createdCustomers.map(CustomerApiOutput(rootUri, _)))
  }
}

