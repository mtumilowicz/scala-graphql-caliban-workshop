package app

import app.gateway.{GraphQlController, GraphQlEnv}
import app.infrastructure.config._
import app.infrastructure.config.http.HttpConfig
import caliban.{CalibanError, GraphQLInterpreter, Http4sAdapter}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder2
import zio.interop.catz._
import zio.{ExitCode => ZExitCode, _}

object Main extends App {
  type AppTask[A] = RIO[DependencyConfig.AppEnv, A]

  override def run(args: List[String]): URIO[ZEnv, ZExitCode] = {
    program
      .provideSomeLayer[ZEnv](DependencyConfig.live.appLayer)
      .orDie
  }

  val program =
    for {
      AppConfig(HttpConfig(port, baseUrl)) <- getAppConfig
      _ <- logging.log.info(s"Starting with $port, baseUrl")
      interpreter <- GraphQlController(baseUrl).interpreter
      _ <- BlazeServerBuilder.apply[AppTask]
        .bindHttp(port, "0.0.0.0")
        .withHttpWebSocketApp(wsBuilder => routes(interpreter, wsBuilder))
        .serve
        .compile
        .drain
    } yield ZExitCode.success

  def routes(implicit interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError],
             wsBuilder: WebSocketBuilder2[AppTask]) =
    Router[AppTask](
      graphQlHttp,
      graphQlWebSocket
    ).orNotFound

  def graphQlHttp(implicit interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError]): (String, HttpRoutes[AppTask]) =
    "/api/graphql" -> Http4sAdapter.makeHttpService(interpreter)

  def graphQlWebSocket(implicit interpreter: GraphQLInterpreter[GraphQlEnv, CalibanError],
                       wsBuilder: WebSocketBuilder2[AppTask]): (String, HttpRoutes[AppTask]) =
    "/ws/graphql"  -> Http4sAdapter.makeWebSocketService(wsBuilder, interpreter)
}
