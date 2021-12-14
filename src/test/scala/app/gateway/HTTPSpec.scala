package app.gateway

import org.http4s._
import zio._
import zio.interop.catz._
import zio.test.Assertion._
import zio.test._

object HTTPSpec {

  def request[F[_]](
                     method: Method,
                     uri: String
                   ): Request[F] = Request(method = method, uri = Uri.fromString(uri).toOption.get)

  def checkStatus[R, A](actualEffect: RIO[R, Response[RIO[R, *]]], expectedStatus: Status): RIO[R, TestResult] =
    for {
      actual <- actualEffect
      status = actual.status
    } yield assert(status)(equalTo(expectedStatus))

  def checkBody[R, A](
                       actualEffect: RIO[R, Response[RIO[R, *]]],
                       expectedBody: A
                        )(implicit
                          ev: EntityDecoder[RIO[R, *], A]
                        ): RIO[R, TestResult] = {
    for {
      actual <- actualEffect
      testResult <- assertM(actual.as[A])(equalTo(expectedBody))
    } yield testResult
  }
}
