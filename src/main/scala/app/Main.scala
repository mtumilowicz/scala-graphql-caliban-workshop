package app

import app.gateway.GraphQlController
import app.infrastructure.config.DependencyConfig.AppEnv
import app.infrastructure.config._
import caliban.Http4sAdapter
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import zio.clock.Clock
import zio.interop.catz._
import zio.{ExitCode => ZExitCode, _}

object Main extends App {
  type AppTask[A] = RIO[DependencyConfig.AppEnv with Clock, A]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ZExitCode] = {
    val prog =
      for {
        cfg <- getAppConfig
        _ <- logging.log.info(s"Starting with $cfg")
        baseUrl = cfg.http.baseUrl
        interpreter <- GraphQlController(baseUrl).interpreter
        httpApp = Router[AppTask](
           "/graphql" -> CORS.policy(Http4sAdapter.makeHttpService(interpreter))
        ).orNotFound
        _ <- runHttp(httpApp, cfg.http.port)
      } yield ZExitCode.success

    prog
      .provideSomeLayer[ZEnv](DependencyConfig.live.appLayer)
      .orDie
  }

  def runHttp[R <: AppEnv with Clock](
                           httpApp: HttpApp[AppTask],
                           port: Int
                         ): ZIO[R, Throwable, Unit] = {
    for {
      _ <- BlazeServerBuilder.apply[AppTask]
        .bindHttp(port, "0.0.0.0")
        .withHttpApp(CORS.policy(httpApp))
        .serve
        .compile
        .drain
    } yield ()
  }
}
