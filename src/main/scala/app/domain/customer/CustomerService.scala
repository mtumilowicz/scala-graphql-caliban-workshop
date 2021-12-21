package app.domain.customer

import app.domain.customerdetails.CustomerDetailsService
import zio.stream.UStream
import zio.{Task, UIO}

case class CustomerService(idService: IdService,
                           customerDetailsService: CustomerDetailsService,
                           repository: CustomerRepository) {

  def createdCustomers: UStream[Customer] =
    repository.createdCustomers

  def getById(id: CustomerId): UIO[Option[CustomerView]] = {
    repository.getById(id).map(_.map(_.toView(customerDetailsService.getById(id))))
  }

  def getByIdWithoutDetails(id: CustomerId): UIO[Option[CustomerView]] = {
    repository.getById(id).map(_.map(_.toView()))
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
