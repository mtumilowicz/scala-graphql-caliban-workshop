package app.domain.customer

import app.domain.order.OrderId

case class NewCustomerCommand(name: String, locked: Boolean) {
  def toCustomer(id: CustomerId): Customer =
    Customer(id = id, name = name, orders = List(OrderId("1")) ,locked = locked)
}
