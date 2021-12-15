package app

import app.domain.customer.CustomerServiceEnv
import zio.clock.Clock
import zio.console.Console

package object gateway {

  type GraphQlEnv = Console with Clock with CustomerServiceEnv

}
