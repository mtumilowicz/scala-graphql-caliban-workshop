package app.gateway

import app.domain.customer.CustomerServiceEnv
import app.gateway.customer.out.CustomerApiOutput
import app.gateway.customer.{CustomerGraphQlMutations, CustomerGraphQlQueries, CustomerGraphQlSubscriptions}
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
  // https://ghostdogpr.github.io/caliban/docs/#subscriptions
  // All the fields of the subscription root case class MUST return ZStream or ? => ZStream objects
  case class Subscriptions(createdCustomers: ZStream[CustomerServiceEnv, Throwable, CustomerApiOutput])

  private val queries = Queries(CustomerGraphQlQueries(baseUrl))
  private val mutations = Mutations(CustomerGraphQlMutations(baseUrl))
  private val subscriptions = Subscriptions(CustomerGraphQlSubscriptions(baseUrl).createdCustomers)

  private val graphQl = graphQL(RootResolver(queries, mutations,subscriptions)) @@
    maxDepth(30) @@
    maxFields(200) @@
    timeout(10 seconds) @@
    printSlowQueries(5 seconds) @@
    printErrors

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[GraphQlEnv, CalibanError]] =
    graphQl.interpreter
}
