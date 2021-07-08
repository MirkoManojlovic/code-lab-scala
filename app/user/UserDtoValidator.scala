package user

import cats.data.ValidatedNec
import cats.implicits._
import play.api.libs.json.{Json, OFormat}

class UserDtoValidator {

  private val userDtoValidation: UserDtoValidation = new SimpleUserDtoValidation

  def validate(userDto: UserDtoForWriteOps): Either[List[UserValidationError], UserDtoForWriteOps] =
    userDtoValidation.validateUserDto(userDto)

}

sealed trait UserValidationError {
  def errorMessage: String
}

case class PasswordsDoNotMatch() extends UserValidationError {
  override def errorMessage: String = "Passwords must match"
}

case class InvalidEmail(emailError: EmailValidationError) extends UserValidationError {
  override def errorMessage: String = emailError.errorMessage
}

case class InvalidPassword(passwordError: PasswordValidationError) extends UserValidationError {
  override def errorMessage: String = passwordError.errorMessage
}

case class InvalidUsernameLength(usernameLength: Int) extends UserValidationError {
  override def errorMessage: String =
    s"Username length is: $usernameLength. Username must not be shorter than 6 and longer than 32 characters"
}

sealed trait UserDtoValidation {

  type UserValidationResult[A] = ValidatedNec[UserValidationError, A]

  private def validateConfirmedPassword(userDto: UserDtoForWriteOps): Either[PasswordsDoNotMatch, UserDtoForWriteOps] =
    Either.cond(userDto.password == userDto.confirmedPassword, userDto, PasswordsDoNotMatch())

  private def validatePassword(passwordForValidation: String): UserValidationResult[Password] =
    Password.fromString(passwordForValidation).fold(
      error => InvalidPassword(error).invalidNec,
      password => password.validNec
    )

  private def validateEmail(emailForValidation: String): UserValidationResult[Email] = {
    Email.fromString(emailForValidation).fold(
      error => InvalidEmail(error).invalidNec,
      password => password.validNec
    )
  }

  private def validateUsername(usernameForValidation: String): UserValidationResult[String] = {
    if (usernameForValidation.length < 6 || usernameForValidation.length > 32)
      InvalidUsernameLength(usernameForValidation.length).invalidNec
    else
      usernameForValidation.validNec
  }

  def validateRestOfDto(userDto: UserDtoForWriteOps): Either[List[UserValidationError], UserDtoForWriteOps] = {
    val UserDtoForWriteOps(_, email, username, password, _) = userDto
    val userDtoCreator = validatedUserDtoCreatorFactory(userDto)
    val result = (
      validatePassword(password),
      validateEmail(email),
      validateUsername(username)
      ).mapN(userDtoCreator)

    result.fold(
      errors => Left(errors.toChain.toList),
      dto => Right(dto)
    )
  }

  private def validatedUserDtoCreatorFactory(userDto: UserDtoForWriteOps): (Password, Email, String) => UserDtoForWriteOps = {
    (validatedPassword: Password, validatedEmail: Email, validatedUsername: String) =>
      userDto.copy(
        password = validatedPassword.passwordValue,
        email = validatedEmail.emailValue,
        username = validatedUsername
      )
  }

  def validateUserDto(userDto: UserDtoForWriteOps): Either[List[UserValidationError], UserDtoForWriteOps] =
    validateConfirmedPassword(userDto) match {
      case Left(e) => Left(List(e))
      case Right(userDto) => validateRestOfDto(userDto)
    }
}

class SimpleUserDtoValidation extends UserDtoValidation {

}
