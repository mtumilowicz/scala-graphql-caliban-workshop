package app.domain.customer

import app.domain.order._
import zio.query.UQuery

case class Customer(id: CustomerId, name: String, locked: Boolean) {
  def toView(details: UQuery[CustomerDetails]): CustomerView = {
    CustomerView(id = id, name = name, details = details, locked = locked)
  }
}
