package app.gateway

import app.domain.customer.{CustomerId, NewCustomerCommand}
import app.domain.customerdetails
import app.infrastructure.config.DependencyConfig
import app.infrastructure.config.customer.CustomerServiceProxy
import io.circe.literal.JsonStringContext
import io.circe.{Json, parser}
import zio.ZIO
import zio.test.Assertion._
import zio.test._

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

        for {
          _ <- result
          customerMaybe <- CustomerServiceProxy.getById(CustomerId("1"))
          customer = customerMaybe.get
          detailsCheck <- assertM(customer.details.run)(isSome(equalTo(customerdetails.CustomerDetails(CustomerId("1"), true))))
        } yield assert(customer.name)(equalTo("MTU test")) &&
          assert(customer.id)(equalTo(CustomerId("1"))) &&
          assert(customer.locked)(isFalse) &&
          detailsCheck
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
