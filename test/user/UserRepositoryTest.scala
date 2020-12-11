package user

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.WordSpec
import org.scalatest.Matchers._

class UserRepositoryTest extends WordSpec {

  "When saving User" should {

    "save user and return it" in {
      val userRepository = new UserRepository
      val mockUser = User(1, "user@email.com", "username", "password")

      userRepository.save(mockUser)
      val returnedUser = userRepository.findById(mockUser.id)

      returnedUser.value should be(mockUser)
    }
  }

}
