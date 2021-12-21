package app.domain.customer

import app.domain.order.CustomerDetails


case class NewCustomerCommand(name: String, locked: Boolean) {
  def toCustomer(id: CustomerId): Customer =
    Customer(id = id, name = name, details = CustomerDetails(id, true) ,locked = locked)
}
