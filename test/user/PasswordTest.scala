package user

import org.scalatest.{EitherValues, Matchers, WordSpec}

class PasswordTest extends WordSpec with EitherValues with Matchers {

  "When creating a password" should {

    "create a password if length is more than 6 characters" in {
      val validPassword = "longpassword"

      val password = Password.fromString(validPassword)

      password.right.value.passwordValue shouldBe validPassword
    }

    "return an invalid length error when password has less than 6 characters" in {
      val invalidPassword = "short"

      val password = Password.fromString(invalidPassword)

      password.left.value shouldBe a [InvalidLength]
    }
  }

}
