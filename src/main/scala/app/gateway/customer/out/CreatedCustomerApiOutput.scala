package app.gateway.customer.out

import app.domain.customer.Customer

final case class CreatedCustomerApiOutput(id: String, url: String, name: String, locked: Boolean)

object CreatedCustomerApiOutput {

  def fromDomain(
                  basePath: String,
                  customer: Customer
                ): CreatedCustomerApiOutput =
    CreatedCustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      customer.locked
    )
}