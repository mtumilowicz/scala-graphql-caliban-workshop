package app.gateway.customer.out

import app.domain.customerdetails.CustomerDetails
import zio.query.UQuery

case class CustomerDetailsApiOutput(id: String, locked: Boolean)

object CustomerDetailsApiOutput {
  def fromDomain(detailsQuery: UQuery[Option[CustomerDetails]]): UQuery[Option[CustomerDetailsApiOutput]] =
    detailsQuery.map(_.map(details => CustomerDetailsApiOutput(id = details.id.value, locked = details.locked)))
}
