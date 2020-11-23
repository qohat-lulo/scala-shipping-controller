package main

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import com.qohat.file.filerw.FilesManager
import com.qohat.shipment.{Repo, RepoConfig, Service, ServiceConfig}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  private val logger =
    Slf4jLogger.getLoggerFromClass[IO](this.getClass)

  override def run(args: List[String]): IO[ExitCode] =
    Blocker[IO].use { blocker =>
      FilesManager.create[IO](blocker).use { implicit files =>
        for {
          _ <- logger.info("Shipping controller starting...")
          _ <- logger.info("Loading config...")
          config <- RepoConfig.config.load[IO]
          _ <- logger.info("Config loaded")
          _ <- logger.info("Creating out folder...")
          _ <- files.createFolder(config.outFolder)
          _ <- logger.info("Out folder created")
          shippingRepo <- Repo.create[IO](config)
          servConf <- ServiceConfig.config.load[IO]
          shippingServ <- Service.create[IO](servConf, shippingRepo)
          _ <- logger.info("Executing shipping service...")
          _ <- shippingServ.execute.compile.drain
          _ <- logger.info(
                 "Seems like all went well, please check the output folder"
               )
        } yield ExitCode.Success
      }
    }
}
