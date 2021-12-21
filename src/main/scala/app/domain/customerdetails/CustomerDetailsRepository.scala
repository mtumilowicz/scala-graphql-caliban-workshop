package app.domain.customerdetails

import app.domain.customer._
import zio.UIO

trait CustomerDetailsRepository {
  def getById(customerIds: Set[CustomerId]): UIO[Map[CustomerId, CustomerDetails]]
}
