package app.domain.customer

import app.domain.order._
import zio.query.UQuery

case class CustomerView(id: CustomerId, name: String, details: UQuery[CustomerDetails], locked: Boolean)