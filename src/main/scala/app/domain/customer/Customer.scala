package app.domain.customer

import app.domain.order._

case class Customer(id: CustomerId, name: String, orders: List[OrderId], locked: Boolean) {
  def toView(getOrders: List[Order] = List(Order(id = OrderId("1"), paid = true))): CustomerView = {
    CustomerView(id = id, name = name, orders = getOrders, locked = locked)
  }
}
