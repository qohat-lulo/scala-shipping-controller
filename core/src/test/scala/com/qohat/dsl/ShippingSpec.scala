package com.qohat.dsl

import com.qohat.Spec
import com.qohat.dsl.Shipping._

final class ShippingSpec extends Spec {

  "move(AAAAIAA)" should {
    "Return Location(-2, 4, West)" in {
      val expected = Location(Abscissa(-2), Ordered(4), West)
      move("AAAAIAA".toList) shouldBe expected
    }
  }

  "move(DDAAIAD)" should {
    "Return Location(1, -2, South)" in {
      val expected = Location(Abscissa(1), Ordered(-2), South)
      move("DDAAIAD".toList) shouldBe expected
    }
  }

  "move(AAIADAD)" should {
    "Return Location(-1, 3, East)" in {
      val expected = Location(Abscissa(-1), Ordered(3), East)
      move("AAIADAD".toList) shouldBe expected
    }
  }

  "move(ADAAIAA)" should {
    "Return Location(2, 3, North)" in {
      val expected = Location(Abscissa(2), Ordered(3), North)
      move("ADAAIAA".toList) shouldBe expected
    }
  }

}
