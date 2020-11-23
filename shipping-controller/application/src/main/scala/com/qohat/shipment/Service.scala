package com.qohat.shipment

import cats.effect.Async
import com.qohat.dsl.Shipping.{Location, Shipper}

final class Service[F[_]: Async] private (
  val config: ServiceConfig.Config,
  val shipmentRepo: ShipmentRepo[F]
) extends ShippingService[F] {

  override def execute: fs2.Stream[F, Unit] =
    shipmentRepo.findAll
      .map(coveredLocations)
      .map(maxShipments)
      .flatMap(shipmentRepo.save)

  private def coveredLocations(shipper: Shipper): Shipper =
    shipper.copy(locations = filterCovered(shipper.locations))

  private def filterCovered(locations: List[Location]): List[Location] =
    locations.filter(l =>
      l.x.value.abs <= config.coverage && l.y.value.abs <= config.coverage
    )

  private def maxShipments(shipper: Shipper): Shipper =
    shipper.copy(locations = shipper.locations.take(config.maxShipments))

}

object Service {
  def create[F[_]: Async](
    config: ServiceConfig.Config,
    shipmentRepo: ShipmentRepo[F]
  ): F[Service[F]] =
    Async[F].delay(new Service[F](config, shipmentRepo))
}
