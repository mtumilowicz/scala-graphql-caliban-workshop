package app.gateway.customer.out

import app.domain.order.CustomerDetails

case class CustomerDetailsApiOutput(id: String, paid: Boolean)

object CustomerDetailsApiOutput {
  def fromDomain(details: CustomerDetails): CustomerDetailsApiOutput =
    CustomerDetailsApiOutput(id = details.id.value, paid = details.paid)
}
