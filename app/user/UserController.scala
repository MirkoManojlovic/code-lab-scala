package user

import javax.inject.{Inject, Singleton}
import play.api.Logging
import play.api.libs.json._
import play.api.mvc._

case class CodeLabHttpError(errorMessage: String)

object CodeLabHttpError {
  implicit val jsonFormat: Writes[CodeLabHttpError] = Json.format[CodeLabHttpError]
}

@Singleton
class UserController @Inject()(
  val controllerComponents: ControllerComponents,
  val userDtoValidator: UserDtoValidator,
  val userMapper: UserMapper
) extends BaseController with Logging {

  def saveUser(): Action[UserDtoForWriteOps] = Action(parse.json[UserDtoForWriteOps]) { request =>
    val userDto = request.body
    logger.info(s"Received user information : $userDto")

    userDtoValidator.validate(userDto).fold(
      errors => handleError(errors),
      validatedUserDto => handleSuccess(validatedUserDto)
    )
  }

  private def handleError(errors: List[UserValidationError]): Result = {
    val httpError = errors.map(e => CodeLabHttpError(e.errorMessage))
    BadRequest(Json.toJson(httpError))
  }

  private def handleSuccess(validatedUserDto: UserDtoForWriteOps): Result = {
    Ok(Json.toJson(saveUser(validatedUserDto)))
  }

  def saveUser(validatedUserDto: UserDtoForWriteOps): UserDtoForReadOps = {
    val user = userMapper.toUser(validatedUserDto)
    logger.info(s"Saving user: $user")
    userMapper.toDto(user)
  }

}
