package app.domain.customer

import zio.{Task, UIO}

case class CustomerService(idService: IdService, repository: CustomerRepository) {

  def getAll: fs2.Stream[Task, Customer] =
    repository.getAll

  def getById(id: CustomerId): UIO[Option[Customer]] =
    repository.getById(id)

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
