package app.infrastructure.config.customerdetails

import app.domain.customer._
import app.domain.customerdetails.{CustomerDetails, CustomerDetailsServiceEnv}
import zio._

object CustomerServiceProxy {

  def getById(customerIds: Set[CustomerId]): URIO[CustomerDetailsServiceEnv, Map[CustomerId, CustomerDetails]] =
    ZIO.accessM(_.get.getByIds(customerIds))

}
