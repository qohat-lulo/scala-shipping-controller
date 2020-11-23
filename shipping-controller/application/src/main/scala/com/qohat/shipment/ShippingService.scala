package com.qohat.shipment

import fs2.Stream

trait ShippingService[F[_]] {
  def execute: Stream[F, Unit]
}
