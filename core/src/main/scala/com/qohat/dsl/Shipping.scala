package com.qohat.dsl

import io.estatico.newtype.macros.newtype

import scala.annotation.tailrec

object Shipping {

  @newtype case class Id(value: String)
  case class Shipper(id: Id, locations: List[Location])

  @newtype case class Abscissa(value: Int)
  @newtype case class Ordered(value: Int)

  sealed trait Direction
  case object North extends Direction
  case object South extends Direction
  case object West extends Direction
  case object East extends Direction

  case class Location(
    x: Abscissa = Abscissa(0),
    y: Ordered = Ordered(0),
    direction: Direction = North
  )

  @tailrec
  def move(commands: List[Char], location: Location = Location()): Location =
    commands match {
      case Nil    => location
      case h :: t => move(t, where(h)(location))
    }

  /**
    * Decides which movement function should call depending of the command
    * @param command
    * @param location
    * @return new Location()
    */
  private def where(command: Char)(location: Location): Location =
    (command, location) match {
      case ('A', l) => forward(l)
      case ('D', l) => turnRight(l)
      case ('I', l) => turnLeft(l)
      case _        => location
    }

  /**
    * Y + 1 when North and X + 1 when East && Y - 1 when South and X - 1 when West
    * @param location
    * @return new Location()
    */
  private def forward(location: Location): Location =
    location match {
      case l @ Location(_, y, North) => l.copy(y = Ordered(y.value + 1))
      case l @ Location(_, y, South) => l.copy(y = Ordered(y.value - 1))
      case l @ Location(x, _, East)  => l.copy(x = Abscissa(x.value + 1))
      case l @ Location(x, _, West)  => l.copy(x = Abscissa(x.value - 1))
      case _                         => location
    }

  /**
    * + 90º depending of the current direction
    * @param location
    * @return new Location()
    */
  private def turnRight(location: Location): Location =
    location match {
      case l @ Location(_, _, North) => l.copy(direction = East)
      case l @ Location(_, _, East)  => l.copy(direction = South)
      case l @ Location(_, _, South) => l.copy(direction = West)
      case l @ Location(_, _, West)  => l.copy(direction = North)
      case _                         => location
    }

  /**
    * - 90º depending of the current direction
    * @param location
    * @return new Location()
    */
  private def turnLeft(location: Location): Location =
    location match {
      case l @ Location(_, _, North) => l.copy(direction = West)
      case l @ Location(_, _, West)  => l.copy(direction = South)
      case l @ Location(_, _, South) => l.copy(direction = East)
      case l @ Location(_, _, East)  => l.copy(direction = North)
      case _                         => location
    }
}
