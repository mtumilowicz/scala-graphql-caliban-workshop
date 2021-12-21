package app.gateway

import app.domain.customer.{CustomerId, CustomerView, NewCustomerCommand}
import app.domain.order.{Order, OrderId}
import app.infrastructure.config.DependencyConfig
import app.infrastructure.config.customer.CustomerServiceProxy
import io.circe.literal.JsonStringContext
import io.circe.{Json, parser}
import zio.ZIO
import zio.test.Assertion.{equalTo, isNone, isSome}
import zio.test.{DefaultRunnableSpec, assertM}

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
      },
      testM("should create new customer 2") {
        val result = for {
          interpreter <- GraphQlController("").interpreter
          _ <- interpreter
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
        } yield ()

        assertM(result.flatMap(_ => CustomerServiceProxy.getById(CustomerId("1"))))(isSome(equalTo(CustomerView(CustomerId("1"), "MTU test", List(Order(OrderId("1"), true)), false))))
      },
      testM("should delete new customer") {
        val actual: ZIO[GraphQlEnv, Throwable, String] = for {
          _ <- CustomerServiceProxy.create(NewCustomerCommand("MTU", true))
          interpreter <- GraphQlController("").interpreter
          result <- interpreter
            .execute(
              """
                |mutation {
                |	customers {
                |		deleteById(value: "1")
                |	}
                |}
                |""".stripMargin)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers":{
                    "deleteById":"1"
                  }
                }"""))
      },
      testM("should delete new customer 2") {
        val result = for {
          _ <- CustomerServiceProxy.create(NewCustomerCommand("MTU", true))
          interpreter <- GraphQlController("").interpreter
          _ <- interpreter
            .execute(
              """
                |mutation {
                |	customers {
                |		deleteById(value: "1")
                |	}
                |}
                |""".stripMargin)
        } yield ()

        assertM(result.flatMap(_ => CustomerServiceProxy.getById(CustomerId("1"))))(isNone)
      }

    ).provideSomeLayer(layer)
}
