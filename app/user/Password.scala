package user

sealed abstract case class Password private(passwordValue: String)

object Password {
  def fromString(passwordValue: String): Either[PasswordValidationError, Password] = {
    if (passwordValue.length > 6)
      Right(new Password(passwordValue) {})
    else
      Left(InvalidLength(passwordValue))
  }
}

sealed trait PasswordValidationError {
  def errorMessage: String
}

case class InvalidLength(passwordValue: String) extends PasswordValidationError {
  override def errorMessage: String = s"Password must be at least 6 characters"
}
