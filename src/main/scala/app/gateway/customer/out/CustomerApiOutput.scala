package app.gateway.customer.out

import app.domain.customer._
import zio.query.UQuery

final case class CustomerApiOutput(id: String, url: String, name: String, details: Option[CustomerDetailsApiOutput])

object CustomerApiOutput {

  def fromDomain(
                  basePath: String,
                  customer: CustomerView
                ): UQuery[CustomerApiOutput] = for {
    details <- CustomerDetailsApiOutput.fromDomain(customer.details)
  } yield CustomerApiOutput(
    customer.id.value,
    s"$basePath/${customer.id.value}",
    customer.name,
    details
  )
}
