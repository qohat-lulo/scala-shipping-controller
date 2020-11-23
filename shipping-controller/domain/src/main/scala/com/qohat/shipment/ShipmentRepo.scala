package com.qohat.shipment

import com.qohat.dsl.Shipping.Shipper
import fs2.Stream

trait ShipmentRepo[F[_]] {
  def findAll: Stream[F, Shipper]
  def save(shipper: Shipper): Stream[F, Unit]
}
