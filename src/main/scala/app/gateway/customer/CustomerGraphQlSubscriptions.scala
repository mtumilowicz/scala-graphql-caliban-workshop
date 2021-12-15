package app.gateway.customer

import zio.stream.ZStream

//case class CustomerGraphQlSubscriptions private(getAll: ZStream[CustomerServiceEnv, Throwable, CustomerApiOutput])
//
//object CustomerGraphQlSubscriptions {
//
//  def apply(baseUrl: String): CustomerGraphQlSubscriptions = {
//    val rootUri = s"$baseUrl/customers"
//
//    CustomerGraphQlSubscriptions(CustomerServiceProxy.getAll.map(CustomerApiOutput(rootUri, _)))
//  }
//}

case class CustomerGraphQlSubscriptions private(getAll: ZStream[Any, Nothing, String])

object CustomerGraphQlSubscriptions {

  def apply(): CustomerGraphQlSubscriptions =
    CustomerGraphQlSubscriptions(ZStream.fromIterable(List("a", "b", "c")))
}

