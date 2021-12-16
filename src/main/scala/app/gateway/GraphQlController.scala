package app.gateway

import app.gateway.customer.{CustomerGraphQlMutations, CustomerGraphQlQueries}
import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers._
import caliban.{CalibanError, GraphQLInterpreter, RootResolver}
import zio.IO
import zio.duration.durationInt
import zio.stream.ZStream

import scala.language.postfixOps

case class GraphQlController(baseUrl: String) extends GenericSchema[GraphQlEnv] {

  case class Queries(customers: CustomerGraphQlQueries)
  case class Mutations(customers: CustomerGraphQlMutations)
  case class Subscriptions(characterDeleted: ZStream[Any, Nothing, String])

  private val queries = Queries(CustomerGraphQlQueries(baseUrl))
  private val mutations = Mutations(CustomerGraphQlMutations(baseUrl))
  private val subscriptions = Subscriptions(ZStream.fromIterable(List("a", "b", "c")))

  private val graphQl = graphQL(RootResolver(queries, mutations,subscriptions)) @@
    maxDepth(30) @@
    maxFields(200) @@
    timeout(10 seconds) @@
    printSlowQueries(5 seconds)

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[GraphQlEnv, CalibanError]] =
    graphQl.interpreter
}
