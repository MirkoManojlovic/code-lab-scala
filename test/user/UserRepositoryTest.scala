package user

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.WordSpec
import org.scalatest.Matchers._

class UserRepositoryTest extends WordSpec {
  private val userRepository = new UserRepository
  private val mockUser = User(1, "user@email.com", "username", "password")

  "When saving User" should {

    "save user and return it" in {
      userRepository.save(mockUser)
      val returnedUser = userRepository.findById(mockUser.id)

      returnedUser.value should be(mockUser)
    }
  }

  "When deleting a User" should {

    "delete a user when user is present" in {
      userRepository.save(mockUser)

      val returnedDeleteUser = userRepository.deleteById(mockUser)

      returnedDeleteUser.value should be(mockUser)
    }

    "return None when user is not present" in {
      val returnedDeleteUser = userRepository.deleteById(mockUser)

      returnedDeleteUser should be(None)
    }
  }

}
