package app

import app.domain.ApiServiceEnv
import zio.clock.Clock
import zio.console.Console

package object gateway {

  type GraphQlEnv = Console with Clock with ApiServiceEnv

}
