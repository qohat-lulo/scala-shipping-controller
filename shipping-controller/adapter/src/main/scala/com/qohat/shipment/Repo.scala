package com.qohat.shipment

import cats.effect.Async
import com.qohat.dsl.Shipping.{Id, Location, Shipper, move}
import com.qohat.file.filerw.{File, FilesRW, Line, Name}
import fs2.Stream

final class Repo[F[_]: Async: FilesRW] private (
  val config: RepoConfig.Config
) extends ShipmentRepo[F] {

  override def findAll: Stream[F, Shipper] =
    FilesRW[F]
      .read(config.maxConcurrent)(config.inFolder)
      .map(toShipper)

  override def save(shipper: Shipper): Stream[F, Unit] =
    FilesRW[F]
      .write(
        config.outFolder,
        toFile(shipper)
      )

  private def toShipper(file: File): Shipper =
    Shipper(
      Id(file.name.value),
      file.lines.map(line => move(line.value.toList))
    )

  private def toFile(shipper: Shipper): File =
    File(
      Name(shipper.id.value),
      shipper.locations
        .map(fromLocation)
        .map(_ + "\n")
        .map(Line)
    )

  private def fromLocation(location: Location): String =
    s"(${location.x}, ${location.y}) ${location.direction}"

}

object Repo {
  def create[F[_]: Async: FilesRW](config: RepoConfig.Config): F[Repo[F]] =
    Async[F].delay(new Repo(config))
}
