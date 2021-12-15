package app.gateway

import app.domain.ApiServiceEnv
import app.gateway.customer.{CustomerGraphQlMutations, CustomerGraphQlQueries, CustomerGraphQlSubscriptions}
import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers._
import caliban.{CalibanError, GraphQLInterpreter, RootResolver}
import zio.IO
import zio.duration.durationInt

import scala.language.postfixOps

case class GraphQlController(baseUrl: String) extends GenericSchema[ApiServiceEnv] {

  case class Queries(customers: CustomerGraphQlQueries)
  case class Mutations(customers: CustomerGraphQlMutations)
  case class Subscriptions(customers: CustomerGraphQlSubscriptions)

  private val queries = Queries(CustomerGraphQlQueries(baseUrl))
  private val mutations = Mutations(CustomerGraphQlMutations(baseUrl))
//  private val subscriptions = Subscriptions(CustomerGraphQlSubscriptions())

  private val graphQl = graphQL(RootResolver(queries, mutations)) @@
    maxDepth(30) @@
    maxFields(200) @@
    timeout(10 seconds) @@
    printSlowQueries(5 seconds)

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[GraphQlEnv, CalibanError]] =
    graphQl.interpreter
}
