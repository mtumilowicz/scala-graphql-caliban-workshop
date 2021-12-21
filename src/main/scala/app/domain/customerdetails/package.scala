package app.domain

import zio.Has

package object customerdetails {
  type CustomerDetailsRepositoryEnv = Has[CustomerDetailsRepository]
  type CustomerDetailsServiceEnv = Has[CustomerDetailsService]
}
