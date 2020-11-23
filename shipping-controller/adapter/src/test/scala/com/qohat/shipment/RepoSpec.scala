package com.qohat.shipment

import java.nio.file.{Path, Paths}

import cats.effect.IO
import com.qohat.SpecEffects
import com.qohat.dsl.Shipping._
import com.qohat.file.filerw._
import com.qohat.shipment.RepoConfig.Config
import fs2.Stream

final class RepoSpec extends SpecEffects {
  private def config: Config =
    Config(Folder("in-test/"), 3, Folder("out-test/"))

  private def successCase: FilesRW[IO] = {
    new FilesRW[IO] {
      override def write(folder: Folder, file: File): Stream[IO, Unit] =
        Stream
          .emits(file.lines)
          .map(_ => ())
          .covary[IO]

      override def read(maxConcurrent: Int)(folder: Folder): Stream[IO, File] =
        Stream.emit(File(Name("01"), List(Line("AAAAIAA"), Line("DDDAIAD"))))

      override def createFolder(folder: Folder): IO[Path] =
        IO { Paths.get("out-test/") }
    }
  }

  "Repo.findAll()" should {
    "transform and get one position" in {
      implicit val filesRW: FilesRW[IO] = successCase
      val effect = for {
        repo <- Repo.create[IO](config)
        result <- repo.findAll.compile.toList
        locations = List(
                      Location(Abscissa(-2), Ordered(4), West),
                      Location(Abscissa(-1), Ordered(-1), West)
                    )
      } yield assertResult(Shipper(Id("01"), locations))(result.head)
      effect.unsafeToFuture()
    }
  }

  "Repo.save()" should {
    "Saved Locations Successfully" in {
      implicit val files: FilesRW[IO] = successCase
      val effect = for {
        repo <- Repo.create[IO](config)
        result <- repo
          .save(
            Shipper(
              Id("01"),
              List(
                Location(Abscissa(-2), Ordered(4), West),
                Location(Abscissa(-1), Ordered(-1), West)
              )
            )
          )
          .compile
          .toList
        assertion = result.size == 2
      } yield assert(assertion)
      effect.unsafeToFuture()
    }
  }
}
