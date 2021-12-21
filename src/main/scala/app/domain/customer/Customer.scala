package app.domain.customer

import app.domain.customerdetails.CustomerDetails
import zio.query.{UQuery, ZQuery}

case class Customer(id: CustomerId, name: String) {
  def toView(details: UQuery[Option[CustomerDetails]] = ZQuery.succeed(None)): CustomerView = {
    CustomerView(id = id, name = name, details = details)
  }
}
