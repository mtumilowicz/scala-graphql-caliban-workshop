package app.gateway.customer.out

import app.domain.customer._

final case class CustomerApiOutput(id: String, url: String, name: String, locked: Boolean)

object CustomerApiOutput {

  def apply(
             basePath: String,
             customer: Customer
           ): CustomerApiOutput =
    CustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      customer.locked
    )
}
