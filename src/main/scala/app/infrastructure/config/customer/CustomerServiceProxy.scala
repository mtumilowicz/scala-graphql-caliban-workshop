package app.infrastructure.config.customer

import app.domain.customer._
import zio._
import zio.stream.ZStream
import zio.stream.interop.fs2z._

object CustomerServiceProxy {

  val getAll: ZStream[CustomerServiceEnv, Throwable, Customer] =
    ZStream.accessStream(_.get.getAll.toZStream())

  val deleteAll: URIO[CustomerServiceEnv, Unit] =
    ZIO.accessM(_.get.deleteAll)

  def create(command: NewCustomerCommand): RIO[CustomerServiceEnv, Customer] =
    ZIO.accessM(_.get.create(command))

  def getById(id: CustomerId): URIO[CustomerServiceEnv, Option[Customer]] =
    ZIO.accessM(_.get.getById(id))

  def delete(id: CustomerId): RIO[CustomerServiceEnv, CustomerId] =
    ZIO.accessM(_.get.delete(id))

}
