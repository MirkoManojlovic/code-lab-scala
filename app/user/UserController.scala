package user

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc._

case class UserDtoRead(id: Int, email: String, username: String, password: String, confirmedPassword: String)
case class UserDtoWrite(id: Int, email: String, username: String)

@Singleton
class UserController @Inject()(val controllerComponents: ControllerComponents,
  val userRepository: UserRepository) extends BaseController {

  implicit val userDtoWrites: OWrites[UserDtoWrite] = Json.writes[UserDtoWrite]
  implicit val userDtoReads: Reads[UserDtoRead] = Json.reads[UserDtoRead]

  def getUserById(id: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val returnedUser = userRepository.findById(id)
    returnedUser.map(user => {
      val userDto = UserDtoWrite(user.id, user.email, user.username)
      Ok(Json.toJson(userDto))
    }).getOrElse(NotFound)
  }

  def saveUser(): Action[UserDtoRead] = Action(parse.json[UserDtoRead]) { implicit request: Request[UserDtoRead] =>
    val userDtoRead = request.body
    val user = User(userDtoRead.id, userDtoRead.email, userDtoRead.username, userDtoRead.password)
    userRepository.save(user)
    val returnedDto = UserDtoWrite(userDtoRead.id, userDtoRead.email, userDtoRead.username)
    Ok(Json.toJson(returnedDto))
  }

}
