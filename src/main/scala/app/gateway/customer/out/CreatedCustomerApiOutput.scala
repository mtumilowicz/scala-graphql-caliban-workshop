package app.gateway.customer.out

import app.domain.customer.Customer

final case class CreatedCustomerApiOutput(id: String, url: String, name: String)

object CreatedCustomerApiOutput {

  def fromDomain(
                  basePath: String,
                  customer: Customer
                ): CreatedCustomerApiOutput =
    CreatedCustomerApiOutput(
      customer.id.raw,
      s"$basePath/${customer.id.raw}",
      customer.name
    )
}