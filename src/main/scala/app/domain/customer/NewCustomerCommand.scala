package app.domain.customer

case class NewCustomerCommand(name: String) {
  def toCustomer(id: CustomerId): Customer =
    Customer(id = id, name = name)
}
