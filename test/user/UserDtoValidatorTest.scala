package user

import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.{EitherValues, Matchers, WordSpec}
import play.api.Logging

class UserDtoValidatorTest extends WordSpec with EitherValues with Matchers with Logging {

  private val userValidator = new UserDtoValidator

  "When validating a UserDto" should {

    "return UserDto object when all input data is correct" in {
      val userDto = UserDtoForWriteOps(Some(1), "user@email.com", "username", "password", "password")

      val result = userValidator.validate(userDto)

      result.right.value shouldBe userDto
    }

    "return error when password and confirmed password do not match" in {
      val userDto = UserDtoForWriteOps(Some(1), "user@email.com", "username", "password", "password1")

      val result = userValidator.validate(userDto)

      result.left.value shouldBe List(PasswordsDoNotMatch())
    }

    "return error when email is invalid" in {
      val invalidEmail = "useremail.com"
      val userDto = UserDtoForWriteOps(Some(1), invalidEmail, "username", "password", "password")

      val result = userValidator.validate(userDto)

      result.left.value shouldBe List(InvalidEmail(MissingAddressSign(invalidEmail)))
    }

    "return error when username is too short" in {
      val invalidUsername = "use"
      val userDto = UserDtoForWriteOps(Some(1), "user@email.com", invalidUsername, "password", "password")

      val result = userValidator.validate(userDto)

      result.left.value shouldBe List(InvalidUsernameLength(invalidUsername.length))
    }

    "return error when username is too long" in {
      val invalidUsername = "useisbadaoidnosdsanpdqasdasded1opmasmpa"
      val userDto = UserDtoForWriteOps(Some(1), "user@email.com", invalidUsername, "password", "password")

      val result = userValidator.validate(userDto)

      result.left.value shouldBe List(InvalidUsernameLength(invalidUsername.length))
    }

    "return error when password is too short" in {
      val invalidPassword = "pass"
      val userDto = UserDtoForWriteOps(Some(1), "user@email.com", "username", invalidPassword, invalidPassword)

      val result = userValidator.validate(userDto)

      result.left.value shouldBe List(InvalidPassword(InvalidLength(invalidPassword)))
    }

    "return list of errors when email, password and username are invalid" in {
      val invalidPassword = "pass"
      val invalidUsername = "use"
      val invalidEmail = "useremail.com"

      val userDto = UserDtoForWriteOps(Some(1), invalidEmail, invalidUsername, invalidPassword, invalidPassword)

      val result = userValidator.validate(userDto)

      result.left.value should contain allOf(
        InvalidPassword(InvalidLength(invalidPassword)),
        InvalidUsernameLength(invalidUsername.length),
        InvalidEmail(MissingAddressSign(invalidEmail))
      )
    }
  }
  "Validating a bad input for user dto" should {
    "return validation errors" in {
      val validEmail = "user@email.com"
      val validUsername = "username"
      val validPassword = "password"

      val invalidEmail = "useremail.com"
      val invalidUsername = "use"
      val invalidPassword = "pass"
      val invalidConfirmedPassword = "p"

      val emailError = InvalidEmail(MissingAddressSign(invalidEmail))
      val usernameError = InvalidUsernameLength(invalidUsername.length)
      val passwordError = InvalidPassword(InvalidLength(invalidPassword))
      val passwordConfirmedError = PasswordsDoNotMatch()

      val data = {
        Table(
          ("email", "username", "password", "confirmedPassword", "error"),
          (invalidEmail, validUsername, validPassword, validPassword, List(emailError)),
          (validEmail, invalidUsername, validPassword, validPassword, List(usernameError)),
          (validEmail, validUsername, invalidPassword, invalidPassword, List(passwordError)),
          (invalidEmail, invalidUsername, invalidPassword, invalidPassword, List(emailError, usernameError, passwordError)),
          (validEmail, validUsername, validPassword, invalidConfirmedPassword, List(passwordConfirmedError)),
        )
      }

      forAll(data) { (email: String, username: String, password: String, confirmedPassword: String, errors: List[UserValidationError]) => {
        logger.info(s"Validating with email: $email, username: $username, password: $password, confirmedPassword: $confirmedPassword")
        logger.info(s"Expecting errors: $errors")
        val userDto = UserDtoForWriteOps(Some(1), email, username, password, confirmedPassword)

        val result = userValidator.validate(userDto)

        result.left.value should contain allElementsOf errors
      }
      }
    }


  }

}
