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

  override def save(shipper: Shipper): Stream[F, Unit] = ???


  private def toShipper(file: File): Shipper =
    Shipper(
      Id(file.name.value),
      file.lines.map(line => move(line.value.toList))
    )

}

object Repo {
  def create[F[_]: Async: FilesRW](config: RepoConfig.Config): F[Repo[F]] =
    Async[F].delay(new Repo(config))
}
