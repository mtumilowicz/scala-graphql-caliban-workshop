package app.domain.customer

import app.domain.customerdetails.CustomerDetails
import zio.query.UQuery

case class Customer(id: CustomerId, name: String) {
  def toView(details: UQuery[Option[CustomerDetails]]): CustomerView = {
    CustomerView(id = id, name = name, details = details)
  }
}
