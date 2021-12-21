package app.domain.customerdetails

import app.domain.customer.CustomerId
import zio.{Chunk, UIO}
import zio.query.{DataSource, Request, UQuery, ZQuery}

case class CustomerDetailsService(repository: CustomerDetailsRepository) {

  private val dataSource: DataSource[Any, GetCustomerDetails] =
    DataSource.fromFunctionBatchedM("CustomerDetailsDataSource")(
      requests => {
        println(requests)
        val ids = requests.map(_.id)
        getByIds(ids.toSet).map(detailsMap => restoreOrder(ids, detailsMap))
      }
    )

  def getById(id: CustomerId): UQuery[Option[CustomerDetails]] =
    ZQuery.fromRequest(GetCustomerDetails(id))(dataSource)

  def getByIds(customerIds: Set[CustomerId]): UIO[Map[CustomerId, CustomerDetails]] =
    repository.getById(customerIds)

  private def restoreOrder(ids: Chunk[CustomerId], map: Map[CustomerId, CustomerDetails]): Chunk[Option[CustomerDetails]] =
    ids.map(id => map.get(id))

  private case class GetCustomerDetails(id: CustomerId) extends Request[Nothing, Option[CustomerDetails]]
}
