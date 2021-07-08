package user

import play.api.libs.json.{Json, OFormat}

sealed abstract case class Email private(emailValue: String)

object Email {
  def fromString(emailValue: String): Either[EmailValidationError, Email] = {
    if (emailValue.contains("@"))
      Right(new Email(emailValue) {})
    else
      Left(MissingAddressSign(emailValue))
  }
}

sealed trait EmailValidationError {
  def errorMessage: String
}

case class MissingAddressSign(emailValue: String) extends EmailValidationError {
  override def errorMessage: String = s"Email: $emailValue must contain @ symbol"
}
