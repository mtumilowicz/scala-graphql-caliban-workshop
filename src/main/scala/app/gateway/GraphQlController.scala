package app.gateway

import app.domain.ApiServiceEnv
import app.gateway.customer.CustomerGraphQlQueries
import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers._
import caliban.{CalibanError, GraphQLInterpreter, RootResolver}
import zio.IO
import zio.duration.durationInt

import scala.language.postfixOps

case class GraphQlController(baseUrl: String) extends GenericSchema[ApiServiceEnv] {

  case class Queries(customers: CustomerGraphQlQueries)

  private val queries = Queries(CustomerGraphQlQueries(baseUrl))

  private val graphQl = graphQL(RootResolver(queries)) @@
    maxDepth(30) @@
    maxFields(200) @@
    timeout(10 seconds) @@
    printSlowQueries(1 second)

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[GraphQlEnv, CalibanError]] =
    graphQl.interpreter
}
