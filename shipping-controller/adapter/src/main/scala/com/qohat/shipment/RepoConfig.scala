package com.qohat.shipment

import cats.implicits._
import ciris.refined._
import ciris.{ConfigValue, _}
import com.qohat.file.filerw.Folder
import eu.timepit.refined.auto._
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString

object RepoConfig {
  type ShippingMaxConcurrent = PosInt
  type InputFolder = NonEmptyString
  type OutputFolder = NonEmptyString

  final case class Config(
    inFolder: Folder,
    maxConcurrent: Int,
    outFolder: Folder
  )

  val config: ConfigValue[Config] =
    (
      env("INPUT_FOLDER")
        .as[InputFolder]
        .default("src/main/resources/in")
        .map(_.value)
        .map(Folder),
      env("SHIPPING_CONCURRENT")
        .as[ShippingMaxConcurrent]
        .default(10)
        .map(_.value),
      env("OUTPUT_FOLDER")
        .as[OutputFolder]
        .default("src/main/resources/out")
        .map(_.value)
        .map(Folder)
    ).parMapN((input, max, output) => Config(input, max, output))
}
