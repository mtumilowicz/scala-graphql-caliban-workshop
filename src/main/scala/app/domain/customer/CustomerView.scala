package app.domain.customer

import app.domain.order._

case class CustomerView(id: CustomerId, name: String, details: CustomerDetails, locked: Boolean)