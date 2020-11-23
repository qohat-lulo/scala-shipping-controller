package com.qohat.shipment

import cats.implicits._
import ciris.refined._
import ciris.{ConfigValue, _}
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt

object ServiceConfig {
  type MaxShipments = PosInt
  type ShippingCoverage = PosInt

  final case class Config(maxShipments: Int, coverage: Int)

  val config: ConfigValue[Config] =
    (
      env("MAX_SHIPMENTS")
        .as[MaxShipments]
        .default(3)
        .map(_.value),
      env("SHIPPING_COVERAGE")
        .as[ShippingCoverage]
        .default(10)
        .map(_.value)
    ).parMapN((shipments, shippers) => Config(shipments, shippers))
}
