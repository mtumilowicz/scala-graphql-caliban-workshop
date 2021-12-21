package app.gateway.customer.out

import app.domain.customer._

final case class CustomerApiOutput(id: String, url: String, name: String, details: CustomerDetailsApiOutput, locked: Boolean)

object CustomerApiOutput {

  def fromDomain(
             basePath: String,
             customer: CustomerView
           ): CustomerApiOutput =
    CustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      CustomerDetailsApiOutput.fromDomain(customer.details),
      customer.locked
    )

  def fromDomain(
                  basePath: String,
                  customer: Customer
                ): CustomerApiOutput =
    CustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      CustomerDetailsApiOutput("1", true),
      customer.locked
    )
}
