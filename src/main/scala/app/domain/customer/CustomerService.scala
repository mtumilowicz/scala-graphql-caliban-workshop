package app.domain.customer

import app.domain.order.CustomerDetails
import zio.query.{DataSource, Request, UQuery, ZQuery}
import zio.stream.UStream
import zio.{Task, UIO, ZIO}

case class CustomerService(idService: IdService, repository: CustomerRepository) {

  case class GetCustomerDetails(id: CustomerId) extends Request[Nothing, CustomerDetails]
  val CustomerDetailsDataSource: DataSource[Any, GetCustomerDetails] =
    DataSource.fromFunctionBatchedM("CustomerDetailsDataSource")(
      requests => ZIO.succeed(requests.map(x => x.id).map(id => CustomerDetails(id, true)))
    )
  def getCustomerDetails(id: CustomerId): UQuery[CustomerDetails] = ZQuery.fromRequest(GetCustomerDetails(id))(CustomerDetailsDataSource)

  def createdCustomers: UStream[Customer] =
    repository.createdCustomers

  def getById(id: CustomerId): UIO[Option[CustomerView]] = {
    repository.getById(id).map(_.map(_.toView(getCustomerDetails(id))))
  }

  def delete(id: CustomerId): Task[CustomerId] =
    repository.delete(id)

  def deleteAll: UIO[Unit] =
    repository.deleteAll

  def create(command: NewCustomerCommand): Task[Customer] = {
    for {
      id <- idService.generate()
      customer = command.toCustomer(CustomerId(id))
      created <- repository.create(customer)
    } yield created
  }

}
