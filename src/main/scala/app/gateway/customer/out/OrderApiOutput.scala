package app.gateway.customer.out

import app.domain.order.Order

case class OrderApiOutput(id: String, paid: Boolean)

object OrderApiOutput {
  def fromDomain(order: Order): OrderApiOutput =
    OrderApiOutput(id = order.id.id, paid = order.paid)
}
