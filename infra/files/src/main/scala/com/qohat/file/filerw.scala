package com.qohat.file

import java.nio.file.{Path, Paths}

import cats.Applicative
import cats.data.Kleisli
import cats.effect.{Blocker, Concurrent, ContextShift, Resource, Sync}
import cats.syntax.flatMap._
import cats.syntax.functor._
import fs2.Stream
import fs2.io.file._
import fs2.text.{lines, _}
import simulacrum.typeclass

package object filerw {

  case class Name(value: String)
  case class Line(value: String)
  case class File(name: Name, lines: List[Line])
  case class Folder(path: String)

  @typeclass
  trait FilesRW[F[_]] {
    def read(maxConcurrent: Int = 1)(folder: Folder): Stream[F, File]
    def write(folder: Folder, file: File): Stream[F, Unit]
    def createFolder(folder: Folder): F[Path]
  }

  final class FilesManager[F[_]: Sync: ContextShift: Concurrent](
    val blocker: Blocker
  ) extends FilesRW[F] {

    override def read(maxConcurrent: Int = 1)(folder: Folder): Stream[F, File] =
      directoryStream(blocker, toPath(folder.path))
        .parEvalMap(maxConcurrent)(readLines)
        .map(tuple => File(tuple._1, tuple._2))

    override def write(folder: Folder, file: File): Stream[F, Unit] =
      Stream
        .emits(file.lines)
        .map(_.value)
        .through(utf8Encode)
        .through(writeAll(outFilePath(folder, file.name), blocker))

    override def createFolder(folder: Folder): F[Path] =
      Kleisli[F, Path, Path] { path =>
        exists(blocker, path)
          .flatMap {
            case true =>
              deleteDirectoryRecursively(blocker, toPath(folder.path))
            case _ => Sync[F].unit
          }
          .flatMap(_ => createDirectory(blocker, path))
      }.run(toPath(folder.path))

    private def readLines(path: Path): F[(Name, List[Line])] =
      readAll(path, blocker, 56)
        .through(utf8Decode)
        .through(lines)
        .filter(_.nonEmpty)
        .map(Line)
        .compile
        .toList
        .map(lines => (toName(path), lines))

    private def outFilePath(folder: Folder, name: Name): Path =
      toPath(s"${folder.path}/out${name.value}.txt")

    private def toPath(path: String): Path =
      Paths.get(path)

    private def toName(path: Path): Name =
      Name(
        path.getFileName.toString
          .replace("in", "")
          .replace(".txt", "")
      )
  }

  object FilesManager {
    def create[F[_]: Sync: ContextShift: Concurrent: Applicative](
      blocker: Blocker
    ): Resource[F, FilesRW[F]] =
      Resource.liftF(Sync[F].delay(new FilesManager[F](blocker)))
  }

}
