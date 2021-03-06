package app

import app.domain.customer._
import app.domain.customerdetails.{CustomerDetailsRepositoryEnv, CustomerDetailsServiceEnv}
import app.domain.id._

package object domain {
  type InternalRepositoryEnv = IdProviderEnv with CustomerDetailsRepositoryEnv
  type InternalServiceEnv = IdServiceEnv with CustomerDetailsServiceEnv
  type ApiRepositoryEnv = CustomerRepositoryEnv
  type ApiServiceEnv = CustomerServiceEnv
}
