package user

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.validation.Constraints._

case class UserDtoRead(id: Int, email: String, username: String, password: String, confirmedPassword: String)
case class UserDtoWrite(id: Int, email: String, username: String)

case class UserData(email: String, username: String, password: String, confirmedPassword: String) {
  def validateMirko() = {
    this.password.equals(this.confirmedPassword)
  }
}

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

  def saveUser2(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val something = userForm.bindFromRequest
//    println(something)
    Ok(something.toString)
  }

  class MirkoCheck[A](val confirmedPasswordKey:String, val map:(String, Map[String, String]) => A, val unmap:A => String) extends Formatter[A] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] = {
      println(key)
      println(data)
      val password = data.getOrElse(key, "")
      val confirmedPassword = data.getOrElse(confirmedPasswordKey, "")
      if (password.isBlank || !password.equals(confirmedPassword)) {
        Left(List(FormError(key, "Passwords must match")))
      } else {
        println("Mirkooooo")
        println(Right(map(key, data)))
        println(Right(map(key, data)))
        Right(map(key, data))
      }
    }

    override def unbind(key: String, value: A): Map[String, String] = Map(key -> unmap(value))
  }

  val confirmedPassword: Mapping[String] = of[String](new MirkoCheck[String]("confirmedPassword", (key, data) => data.getOrElse(key, ""), str => str))


  val userForm = Form(
    mapping(
      "email" -> email ,
      "username"  -> number.verifying(min(0), max(100)),
      "password"  -> confirmedPassword,
      "confirmedPassword"  -> text
    )(UserData.apply)(UserData.unapply).verifying("Passwords must match", userData => {
      println(userData)
//      fields.password.equals(fields.confirmedPassword)
      userData.validateMirko()
    })
  )

  //  class MatchConstraint[A](val targetField:String, val map:(String, Map[String, String]) => A, val unmap:A => String) extends Formatter[A] {
//    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], A] = {
//      val first = data.getOrElse(key, "")
//      val second = data.getOrElse(targetField, "")
//      if (first == "" || !first.equals(second)) {
//        Left(List(FormError(key, "Not Match!")))
//      }
//      else {
//        Right(map(key, data))
//      }
//    }
//
//    override def unbind(key: String, value: A): Map[String, String] = Map(key -> unmap(value))
//  }

//  val registerForm = Form(
//    mapping(
//      "email" -> email.verifying(minLength(6)),
//      "password" -> text(minLength = 6),
//      "passwordConfirmation" -> of(new MatchConstraint[String]("password", (key, data) => data.getOrElse(key, ""), str => str))
//    )(RegisterData.apply)(RegisterData.unapply)
//  )
//
  import play.api.data.format._
  val mirko = Formats.stringFormat
  val passwordOriginal: Mapping[String] = of[String](mirko).verifying(s => mirkoCheck(s))

//  val passwordCheck: Mapping[String] = nonEmptyText(minLength = 10)
//    .verifying(passwordCheckConstraint)

  def mirkoCheck(s:String): Boolean = {
    true
  }

//  val passwordsMatchConstraint: Constraint[String] = Constraint("constraints.passwordsMatch")({ plainText =>
//
//    val errors = plainText match {
//      case allNumbers() => Seq(ValidationError("Password is all numbers"))
//      case allLetters() => Seq(ValidationError("Password is all letters"))
//      case _            => Nil
//    }
//    if (errors.isEmpty) {
//      Valid
//    } else {
//      Invalid(errors)
//    }
//  })

}
