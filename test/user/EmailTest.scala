package user

import org.scalatest.{EitherValues, Matchers, WordSpec}

class EmailTest extends WordSpec with EitherValues with Matchers {

  "When mapping EmailDto to " should {

    "map to Email when data is valid" in {
      val validEmail = "mirko@email.com"

      val result = Email.fromString(validEmail)

      result.right.value.emailValue shouldBe validEmail

    }

    "return error when data is invalid" in {
      val invalidEmail = "mirko"

      val result = Email.fromString(invalidEmail)

      result.left.value shouldBe a[MissingAddressSign]
    }
  }

}
