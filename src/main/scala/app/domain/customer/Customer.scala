package app.domain.customer

import app.domain.order._

case class Customer(id: CustomerId, name: String, locked: Boolean) {
  def toView(details: CustomerDetails = CustomerDetails(id, true)): CustomerView = {
    CustomerView(id = id, name = name, details = details, locked = locked)
  }
}
