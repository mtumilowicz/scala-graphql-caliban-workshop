package app.gateway.customer

import app.domain.customer._
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import zio.URIO
import zio.query.UQuery

case class CustomerGraphQlQueries private (findById: CustomerId => URIO[CustomerServiceEnv, Option[UQuery[CustomerApiOutput]]])

object CustomerGraphQlQueries {

  def apply(baseUrl: String): CustomerGraphQlQueries = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlQueries(id => CustomerServiceProxy.getById(id).map(_.map(CustomerApiOutput.fromDomain(rootUri, _))))
  }
}
