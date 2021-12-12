package app.gateway

import app.domain.CustomerServiceEnv
import app.gateway.customer.CustomerGraphQlQueries
import caliban.GraphQL.graphQL
import caliban.schema.GenericSchema
import caliban.{CalibanError, GraphQLInterpreter, RootResolver}
import zio.IO

case class GraphQlController(baseUrl: String) extends GenericSchema[CustomerServiceEnv] {

  case class Queries(customers: CustomerGraphQlQueries)

  private val queries = Queries(CustomerGraphQlQueries(baseUrl))

  val interpreter: IO[CalibanError.ValidationError, GraphQLInterpreter[CustomerServiceEnv, CalibanError]] =
    graphQL(RootResolver(queries)).interpreter
}
