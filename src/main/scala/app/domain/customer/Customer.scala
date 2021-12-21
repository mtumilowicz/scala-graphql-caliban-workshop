package app.domain.customer

import app.domain.order._

case class Customer(id: CustomerId, name: String, details: CustomerDetails, locked: Boolean) {
  def toView: CustomerView = {
    CustomerView(id = id, name = name, details = details, locked = locked)
  }
}
