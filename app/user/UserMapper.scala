package user

import play.api.Logging

class UserMapper extends Logging {

  def toUser(userDto: UserDtoForWriteOps): User = {
    val result = for {
      email <- Email.fromString(userDto.email)
      password <- Password.fromString(userDto.password)
    } yield User(userDto.id, email, userDto.username, password)
    result.toOption.get
  }

  def toDto(user: User): UserDtoForReadOps = {
    UserDtoForReadOps(
      user.id,
      user.email.emailValue,
      user.username
    )
  }

}
