package app

import app.infrastructure.config.DependencyConfig.AppEnv
import zio.clock.Clock
import zio.console.Console

package object gateway {

  type GraphQlEnv = Console with Clock with AppEnv

}
