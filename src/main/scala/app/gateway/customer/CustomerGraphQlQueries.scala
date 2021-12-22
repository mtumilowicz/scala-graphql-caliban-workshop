package app.gateway.customer

import app.domain.customer._
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.customer.CustomerServiceProxy
import caliban.execution.Field
import zio.URIO
import zio.query.UQuery

case class FindByIdArg(id: String) {
  def toCustomerId: CustomerId = CustomerId(id)
}
case class CustomerGraphQlQueries private(findById: Field => FindByIdArg => URIO[CustomerServiceEnv, Option[UQuery[CustomerApiOutput]]])

object CustomerGraphQlQueries {

  def apply(baseUrl: String): CustomerGraphQlQueries = {
    val rootUri = s"$baseUrl/customers"

    CustomerGraphQlQueries(field => findById(field).andThen(_.map(_.map(CustomerApiOutput.fromDomain(rootUri, _)))))
  }

  private def findById(field: Field): FindByIdArg => URIO[CustomerServiceEnv, Option[CustomerView]] =
    if (field.fields.map(_.name).contains("details")) {
      args => CustomerServiceProxy.getById(args.toCustomerId)
    } else {
      args => CustomerServiceProxy.getByIdWithoutDetails(args.toCustomerId)
    }
}
