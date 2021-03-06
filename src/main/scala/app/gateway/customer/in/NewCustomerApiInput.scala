package app.gateway.customer.in

import app.domain.customer.NewCustomerCommand
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class NewCustomerApiInput(name: String) {
  def toDomain: NewCustomerCommand = NewCustomerCommand(name = name)
}

object NewCustomerApiInput {
  implicit val decoder: Decoder[NewCustomerApiInput] = deriveDecoder
}