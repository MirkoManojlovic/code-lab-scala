package user

import play.api.libs.json.{Json, OFormat, OWrites, Reads, Writes}

case class User(
  id: Option[Int],
  email: Email,
  username: String,
  password: Password
)

case class UserDtoForWriteOps(
  id: Option[Int],
  email: String,
  username: String,
  password: String,
  confirmedPassword: String
)

object UserDtoForWriteOps {
  implicit val jsonFormat: OFormat[UserDtoForWriteOps] = Json.format[UserDtoForWriteOps]
}

case class UserDtoForReadOps(
  id: Option[Int],
  email: String,
  username: String,
)

object UserDtoForReadOps{
  implicit val jsonFormat: OFormat[UserDtoForReadOps] = Json.format[UserDtoForReadOps]
}