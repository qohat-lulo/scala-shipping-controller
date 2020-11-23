package com.qohat

import cats.effect.{Blocker, ContextShift, IO, Timer}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterEach, Matchers}

import scala.concurrent.ExecutionContext

trait SpecEffects extends AsyncWordSpec with Matchers with BeforeAndAfterEach {

  protected implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  protected implicit def timer: Timer[IO] = IO.timer(ExecutionContext.global)

  protected def blocker: Blocker =
    Blocker.liftExecutionContext(ExecutionContext.global)
}
