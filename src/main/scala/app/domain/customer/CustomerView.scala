package app.domain.customer

import app.domain.customerdetails.CustomerDetails
import zio.query.UQuery

case class CustomerView(id: CustomerId, name: String, details: UQuery[Option[CustomerDetails]], locked: Boolean)