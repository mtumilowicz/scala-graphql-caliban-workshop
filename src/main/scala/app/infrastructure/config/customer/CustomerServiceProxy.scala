package app.infrastructure.config.customer

import app.domain.customer._
import zio._
import zio.stream.ZStream

object CustomerServiceProxy {

  val createdCustomers: ZStream[CustomerServiceEnv, Throwable, Customer] =
    ZStream.accessStream(_.get.createdCustomers)

  val deleteAll: URIO[CustomerServiceEnv, Unit] =
    ZIO.accessM(_.get.deleteAll)

  def create(command: NewCustomerCommand): RIO[CustomerServiceEnv, Customer] =
    ZIO.accessM(_.get.create(command))

  def getById(id: CustomerId): URIO[CustomerServiceEnv, Option[CustomerView]] =
    ZIO.accessM(_.get.getById(id))

  def getByIdWithoutDetails(id: CustomerId): URIO[CustomerServiceEnv, Option[CustomerView]] =
    ZIO.accessM(_.get.getByIdWithoutDetails(id))

  def delete(id: CustomerId): RIO[CustomerServiceEnv, CustomerId] =
    ZIO.accessM(_.get.delete(id))

}
