import sbt._

object Dependencies {

  object Versions {
    val betterMonadicFor = "0.3.1"
    val caliban          = "1.3.0"
    val circe            = "0.14.1"
    val http4s           = "0.23.7"
    val jawn             = "1.3.0"
    val kindProjector    = "0.13.2"
    val log4j            = "2.14.1"
    val organizeImports  = "0.6.0"
    val pureConfig       = "0.17.1"
    val zio              = "1.0.12"
    val zioInteropCats   = "3.2.9.0"
    val zioLogging       = "0.5.14"
  }
  import Versions._

  val App =
    List(
      "com.github.ghostdogpr"         %% "caliban"                       % caliban,
      "com.github.ghostdogpr"         %% "caliban-http4s"                % caliban,
      "com.github.pureconfig"        %% "pureconfig"          % pureConfig,
      "dev.zio"                      %% "zio-interop-cats"    % zioInteropCats,
      "dev.zio"                      %% "zio-logging-slf4j"   % zioLogging,
      "dev.zio"                      %% "zio-logging"         % zioLogging,
      "dev.zio"                      %% "zio-test-sbt"        % zio   % "test",
      "dev.zio"                      %% "zio-test"            % zio   % "test",
      "dev.zio"                      %% "zio"                 % zio,
      "io.circe"                     %% "circe-core"          % circe,
      "io.circe"                     %% "circe-generic"       % circe,
      "io.circe"                     %% "circe-literal"       % circe % "test",
      "org.apache.logging.log4j"      % "log4j-api"           % log4j,
      "org.apache.logging.log4j"      % "log4j-core"          % log4j,
      "org.apache.logging.log4j"      % "log4j-slf4j-impl"    % log4j,
      "org.http4s"                   %% "http4s-blaze-server" % http4s,
      "org.http4s"                   %% "http4s-circe"        % http4s,
      "org.http4s"                   %% "http4s-dsl"          % http4s,
      "org.typelevel"                %% "jawn-parser"         % jawn  % "test",
      compilerPlugin("com.olegpy" %% "better-monadic-for" % betterMonadicFor),
      compilerPlugin(("org.typelevel" % "kind-projector"      % kindProjector).cross(CrossVersion.full))
    )

  val ScalaFix =
    List(
      "com.github.liancheng" %% "organize-imports" % organizeImports
    )

}
