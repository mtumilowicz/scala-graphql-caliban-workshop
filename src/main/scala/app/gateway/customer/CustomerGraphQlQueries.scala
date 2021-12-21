package app.gateway.customer

import app.domain.customer._
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import caliban.execution.Field
import zio.URIO
import zio.query.UQuery

case class CustomerGraphQlQueries private(findById: Field => CustomerId => URIO[CustomerServiceEnv, Option[UQuery[CustomerApiOutput]]])

object CustomerGraphQlQueries {

  def apply(baseUrl: String): CustomerGraphQlQueries = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlQueries(field => findById(field).andThen(_.map(_.map(CustomerApiOutput.fromDomain(rootUri, _)))))
  }

  private def findById(field: Field): CustomerId => URIO[CustomerServiceEnv, Option[CustomerView]] =
    if (field.fields.map(_.name).contains("details")) {
      CustomerServiceProxy.getById
    } else {
      CustomerServiceProxy.getByIdWithoutDetails
    }
}
