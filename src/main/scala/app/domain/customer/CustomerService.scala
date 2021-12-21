package app.domain.customer

import app.domain.customerdetails.{CustomerDetails, CustomerDetailsService}
import zio.query.{DataSource, Request, UQuery, ZQuery}
import zio.stream.UStream
import zio.{Task, UIO}

case class CustomerService(idService: IdService,
                           customerDetailsService: CustomerDetailsService,
                           repository: CustomerRepository) {

  case class GetCustomerDetails(id: CustomerId) extends Request[Nothing, Option[CustomerDetails]]
  val CustomerDetailsDataSource: DataSource[Any, GetCustomerDetails] =
    DataSource.fromFunctionBatchedM("CustomerDetailsDataSource")(
      requests => {
        println(requests)
        customerDetailsService.getByIds(requests.map(_.id).toSet)
          .map(x => requests.map(_.id).map(id => x.get(id)))
      }
    )
  def getCustomerDetails(id: CustomerId): UQuery[Option[CustomerDetails]] = ZQuery.fromRequest(GetCustomerDetails(id))(CustomerDetailsDataSource)

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
