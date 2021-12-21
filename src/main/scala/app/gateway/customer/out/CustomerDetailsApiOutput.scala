package app.gateway.customer.out

import app.domain.order.CustomerDetails
import zio.query.UQuery

case class CustomerDetailsApiOutput(id: String, paid: Boolean)

object CustomerDetailsApiOutput {
  def fromDomain(detailsQuery: UQuery[CustomerDetails]): UQuery[CustomerDetailsApiOutput] =
    detailsQuery.map(details => CustomerDetailsApiOutput(id = details.id.value, paid = details.paid))
}
