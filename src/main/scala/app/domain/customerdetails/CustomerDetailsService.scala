package app.domain.customerdetails

import app.domain.customer.CustomerId
import zio.UIO

case class CustomerDetailsService(repository: CustomerDetailsRepository) {

  def getByIds(customerIds: Set[CustomerId]): UIO[Map[CustomerId, CustomerDetails]] =
    repository.getById(customerIds)

}
