package app.gateway

import caliban.{CalibanError, GraphQLInterpreter, GraphQLResponse}
import zio.URIO

object CustomerLifecycle {

  def getById(id: String, interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError]):
  URIO[GraphQlEnv, GraphQLResponse[CalibanError]] =
    interpreter
      .execute(
        s"""
           |query {
           |	customers {
           |		findById(id: "$id") {
           |			id
           |     url
           |			name
           |     details {
           |       locked
           |     }
           |		}
           |	}
           |}
           |""".stripMargin)

  def create(name: String, interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError]):
  URIO[GraphQlEnv, GraphQLResponse[CalibanError]] =
    interpreter
      .execute(
        s"""
          |mutation a {
          |	customers {
          |		create(name: "$name") {
          |			id
          |     url
          |     name
          |		}
          |	}
          |}
          |""".stripMargin)

  def deleteById(id: String, interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError]):
  URIO[GraphQlEnv, GraphQLResponse[CalibanError]] =
  interpreter
    .execute(
      s"""
        |mutation {
        |	customers {
        |		deleteById(id: "$id")
        |	}
        |}
        |""".stripMargin)

}
