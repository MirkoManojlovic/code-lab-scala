package user

import org.scalatest.{EitherValues, Matchers, WordSpec}

class UserMapperTest extends WordSpec with Matchers with EitherValues {

  private val userMapper = new UserMapper

  "When mapping from User to dto and vice versa" should {

    "map user dto to user model" in {
      val userDto = UserDtoForWriteOps(None, "user@email.com", "username", "password", "password")

      val returnedUser = userMapper.toUser(userDto)

      val expectedUser = for {
        email <- Email.fromString(userDto.email)
        password <- Password.fromString(userDto.password)
      } yield User(userDto.id, email, userDto.username, password)

      expectedUser.right.value shouldBe returnedUser
    }

    "map user model to user dto" in {
      val expectedUserDto = UserDtoForReadOps(None, "user@email.com", "username")
      for {
        email <- Email.fromString("user@email.com")
        password <- Password.fromString("password")
        user = User(None, email, "username", password)
        returnedUserDto = userMapper.toDto(user)
      }  yield returnedUserDto shouldBe expectedUserDto
    }
  }

}
