package user

class UserRepository {
  private val userDb = scala.collection.mutable.Map[Int, User]()

  def save(user: User): User = {
    userDb.put(user.id, user)
    user
  }

  def findById(id: Int): Option[User] = userDb.get(id)

}
