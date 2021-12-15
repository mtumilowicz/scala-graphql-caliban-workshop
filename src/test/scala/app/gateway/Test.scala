package app.gateway

import app.domain.customer.NewCustomerCommand
import app.infrastructure.config.DependencyConfig
import app.infrastructure.config.customer.CustomerServiceProxy
import io.circe.{Json, parser}
import io.circe.literal.JsonStringContext
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assertM}
import zio.{ZEnv, ZIO}

object Test extends DefaultRunnableSpec  {

  val layer = DependencyConfig.inMemory.appLayer

  override def spec =
    suite("CustomerService")(
      testM("should query new customer") {
        val actual: ZIO[GraphQlEnv, Throwable, String] = for {
          _ <- CustomerServiceProxy.create(NewCustomerCommand("MTU", true))
          interpreter <- GraphQlController("").interpreter
          result <- interpreter
            .execute(
              """
                |query {
                |	customers {
                |		findById(value: "1") {
                |			id
                |			name
                |		}
                |	}
                |}
                |""".stripMargin)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers":{
                    "findById":{
                      "id":"1",
                      "name":"MTU"
                    }
                  }
                }"""))
      },
      testM("should create new customer") {
        val actual: ZIO[GraphQlEnv, Throwable, String] = for {
          interpreter <- GraphQlController("").interpreter
          result <- interpreter
            .execute(
              """
                |mutation a {
                |	customers {
                |		create(name: "MTU test") {
                |			id
                |     name
                |		}
                |	}
                |}
                |""".stripMargin)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers": {
                    "create": {
                      "id": "1",
                      "name": "MTU test"
                    }
                  }
                }"""))
      }
    ).provideSomeLayer[ZEnv](layer)
}
