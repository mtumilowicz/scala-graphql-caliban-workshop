package app

import app.gateway.GraphQlController
import app.infrastructure.config._
import caliban.Http4sAdapter
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.CORS
import zio.clock.Clock
import zio.interop.catz._
import zio.{ExitCode => ZExitCode, _}

object Main extends App {
  type AppTask[A] = RIO[DependencyConfig.AppEnv with Clock, A]

  override def run(args: List[String]): URIO[ZEnv, ZExitCode] = {
    val prog =
      for {
        cfg <- getAppConfig
        _ <- logging.log.info(s"Starting with $cfg")
        baseUrl = cfg.http.baseUrl
        interpreter <- GraphQlController(baseUrl).interpreter
        _ <- BlazeServerBuilder.apply[AppTask]
          .bindHttp(cfg.http.port, "0.0.0.0")
          .withHttpWebSocketApp(wsBuilder =>
            Router[AppTask](
              "/api/graphql" -> CORS.policy(Http4sAdapter.makeHttpService(interpreter)),
              "/ws/graphql"  -> CORS.policy(Http4sAdapter.makeWebSocketService(wsBuilder, interpreter)),
            ).orNotFound
          )
          .serve
          .compile
          .drain
      } yield ZExitCode.success

    prog
      .provideSomeLayer[ZEnv](DependencyConfig.live.appLayer)
      .orDie
  }
}
