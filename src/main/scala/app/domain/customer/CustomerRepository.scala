package app.domain.customer

import zio.stream.UStream
import zio.{Task, UIO}

trait CustomerRepository extends Serializable {

  def createdCustomers: UStream[Customer]

  def getById(id: CustomerId): UIO[Option[Customer]]

  def delete(id: CustomerId): Task[CustomerId]

  def deleteAll: UIO[Unit]

  def create(customer: Customer): UIO[Customer]
}
