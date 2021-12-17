package app.infrastructure.customer

import app.domain.customer._
import zio._
import zio.stream.{UStream, ZStream}

final private class CustomerInMemoryRepository(ref: Ref[Map[CustomerId, Customer]], subscribers: Hub[Customer])
  extends CustomerRepository {

  override def createdCustomers: UStream[Customer] =
    ZStream.unwrapManaged(subscribers.subscribe.map(ZStream.fromQueue(_)))

  override def delete(id: CustomerId): Task[CustomerId] = {
    ref.update(store => store - id)
      .map(_ => id)
  }

  override def getById(id: CustomerId): UIO[Option[Customer]] =
    ref.get.map(_.get(id))

  override def deleteAll: UIO[Unit] =
    ref.update(_.empty)

  override def create(customer: Customer): UIO[Customer] =
    for {
      _ <- ref.update(store => store + (customer.id -> customer))
      _ <- subscribers.publish(customer)
    } yield customer
}

object CustomerInMemoryRepository {

  val live: URLayer[Any, CustomerRepositoryEnv] =
    ZLayer.fromEffect {
      for {
        ref <- Ref.make(Map.empty[CustomerId, Customer])
        subscribers <- Hub.unbounded[Customer]
      } yield new CustomerInMemoryRepository(ref, subscribers)
    }
}
