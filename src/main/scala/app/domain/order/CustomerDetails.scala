package app.domain.order

import app.domain.customer.CustomerId

case class CustomerDetails(id: CustomerId, paid: Boolean)
