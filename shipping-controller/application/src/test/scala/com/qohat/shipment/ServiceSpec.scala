package com.qohat.shipment

import cats.effect.IO
import com.qohat.SpecEffects
import com.qohat.dsl.Shipping._
import fs2.Stream

final class ServiceSpec extends SpecEffects {

  private def config: ServiceConfig.Config = ServiceConfig.Config(3, 10)

  private def successCase: ShipmentRepo[IO] =
    new ShipmentRepo[IO] {
      override def findAll: Stream[IO, Shipper] = {
        Stream(
          Shipper(
            Id("ID"),
            List(
              Location(Abscissa(-2), Ordered(-19), North),
              Location(Abscissa(-4), Ordered(-1), South)
            )
          ),
          Shipper(
            Id("ID"),
            List(
              Location(Abscissa(-2), Ordered(-19), North),
              Location(Abscissa(-4), Ordered(-12), South)
            )
          )
        )
      }

      override def save(shipper: Shipper): Stream[IO, Unit] =
        Stream.emit(())
    }

  "execute" should {
    "save the locations in a folder" in {
      val effect = for {
        service <- Service.create[IO](config, successCase)
        result <- service.execute.compile.toList
        assertion = result.size == 2
      } yield assert(assertion)
      effect.unsafeToFuture()
    }
  }
}
