package app.gateway.customer.out

import app.domain.customer._

final case class CustomerApiOutput(id: String, url: String, name: String, orders: List[OrderApiOutput], locked: Boolean)

object CustomerApiOutput {

  def fromDomain(
             basePath: String,
             customer: CustomerView
           ): CustomerApiOutput =
    CustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      customer.orders.map(OrderApiOutput.fromDomain),
      customer.locked
    )

  def fromDomain(
                  basePath: String,
                  customer: Customer
                ): CustomerApiOutput =
    CustomerApiOutput(
      customer.id.value,
      s"$basePath/${customer.id.value}",
      customer.name,
      List(OrderApiOutput("1", true)),
      customer.locked
    )
}
