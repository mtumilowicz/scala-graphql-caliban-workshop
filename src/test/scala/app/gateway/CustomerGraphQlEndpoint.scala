package app.gateway

import app.infrastructure.config.DependencyConfig
import io.circe.literal.JsonStringContext
import io.circe.{Json, parser}
import zio.ZIO
import zio.test.Assertion._
import zio.test._

object CustomerGraphQlEndpoint extends DefaultRunnableSpec  {

  val layer = DependencyConfig.inMemory.appLayer

  override def spec =
    suite("CustomerGraphQl")(
      testM("create new customer then verify response") {
        val actual: ZIO[GraphQlEnv, Throwable, String] = for {
          interpreter <- GraphQlController("").interpreter
          result <- CustomerLifecycle.create("MTU test", interpreter)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers": {
                    "create": {
                      "id": "1",
                      "url": "/customers/1",
                      "name": "MTU test"
                    }
                  }
                }"""))
      },
      testM("create new customer then query it and verify") {
        val actual = for {
          interpreter <- GraphQlController("").interpreter
          _ <- CustomerLifecycle.create("MTU test", interpreter)
          result <- CustomerLifecycle.getById("1", interpreter)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers": {
                    "findById": {
                      "id": "1",
                      "url": "/customers/1",
                      "name": "MTU test",
                      "details": {
                        "locked": true
                      }
                    }
                  }
                }"""))
      },
      testM("delete new customer then verify response") {
        val actual: ZIO[GraphQlEnv, Throwable, String] = for {
          interpreter <- GraphQlController("").interpreter
          _ <- CustomerLifecycle.create("MTU test", interpreter)
          result <- CustomerLifecycle.deleteById("1", interpreter)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers":{
                    "deleteById":"1"
                  }
                }"""))
      },
      testM("delete new customer then query it and verify") {
        val actual = for {
          interpreter <- GraphQlController("").interpreter
          _ <- CustomerLifecycle.create("MTU test", interpreter)
          _ <- CustomerLifecycle.deleteById("1", interpreter)
          result <- CustomerLifecycle.getById("1", interpreter)
        } yield result.data.toString

        assertM(actual.map(parser.parse(_).getOrElse(Json.Null)))(equalTo(
          json"""
                {
                  "customers" : {
                    "findById" : null
                  }
                }"""))
      }

    ).provideSomeLayer(layer)
}
