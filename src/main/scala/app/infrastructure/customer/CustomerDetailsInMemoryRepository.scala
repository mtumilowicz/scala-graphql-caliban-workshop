package app.infrastructure.customer

import app.domain.customer._
import app.domain.customerdetails.{CustomerDetails, CustomerDetailsRepository, CustomerDetailsRepositoryEnv}
import zio._

final private class CustomerDetailsInMemoryRepository(ref: Ref[Map[CustomerId, CustomerDetails]])
  extends CustomerDetailsRepository {

  override def getById(customerIds: Set[CustomerId]): UIO[Map[CustomerId, CustomerDetails]] =
    ref.get.map(x => x.view.filterKeys(key => customerIds.contains(key)).toMap)

}

object CustomerDetailsInMemoryRepository {

  val live: URLayer[Any, CustomerDetailsRepositoryEnv] = {
    val init = List(
      (CustomerId("1"), CustomerDetails(CustomerId("1"), true)),
      (CustomerId("2"), CustomerDetails(CustomerId("2"), false)),
      (CustomerId("3"), CustomerDetails(CustomerId("3"), false)),
      (CustomerId("4"), CustomerDetails(CustomerId("4"), true))
    )
    ZLayer.fromEffect {
      for {
        ref <- Ref.make(Map.from(init))
      } yield new CustomerDetailsInMemoryRepository(ref)
    }
  }
}


