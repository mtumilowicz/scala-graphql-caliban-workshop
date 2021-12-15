package app.domain.customer

import zio.{Task, UIO}

trait CustomerRepository extends Serializable {

  def getAll: fs2.Stream[Task, Customer]

  def getById(id: CustomerId): UIO[Option[Customer]]

  def delete(id: CustomerId): Task[CustomerId]

  def deleteAll: UIO[Unit]

  def create(customer: Customer): UIO[Customer]
}
