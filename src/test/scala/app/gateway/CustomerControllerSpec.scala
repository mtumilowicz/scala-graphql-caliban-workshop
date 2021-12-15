package app.gateway

import app.domain.customer._
import app.gateway.HTTPSpec._
import app.gateway.customer.CustomerHttpController
import app.gateway.customer.out.CustomerApiOutput
import app.infrastructure.config.DependencyConfig
import io.circe.Decoder
import io.circe.literal._
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import zio._
import zio.interop.catz._
import zio.test._

object CustomerControllerSpec extends DefaultRunnableSpec {

  type CustomerTask[A] = RIO[CustomerServiceEnv, A]

  val app = CustomerHttpController[CustomerServiceEnv]("").routes.orNotFound

  override def spec =
    suite("CustomerService")(
      testM("should delete customer by id") {
        val setupReq =
          request[CustomerTask](Method.POST, "/")
            .withEntity(json"""{"name": "Test"}""")
        val deleteReq =
          (id: String) => request[CustomerTask](Method.DELETE, s"/$id")
        val req = request[CustomerTask](Method.GET, "/id")
        checkStatus(
          app
            .run(setupReq)
            .flatMap { resp =>
              implicit def circeJsonDecoder[A](implicit
                                               decoder: Decoder[A]
                                              ): EntityDecoder[CustomerTask, A] = jsonOf[CustomerTask, A]

              resp.as[CustomerApiOutput].map(_.id)
            }
            .flatMap(id => app.run(deleteReq(id))) *> app.run(req),
          Status.NotFound
        )
      }
    ).provideSomeLayer[ZEnv](DependencyConfig.inMemory.appLayer)
}
