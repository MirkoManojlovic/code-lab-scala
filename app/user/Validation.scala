package user

import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Reads}

class Validation {

  //  implicit val userReads = Json.reads[User]
  //    implicit val userReads: Reads[UserDto] = (
  //    (JsPath \ "id").read[Int](min(1)) and
  //    (JsPath \ "email").read[String](email) and
  //    (JsPath \ "username").read[String] and
  //    (JsPath \ "password").read[String] and
  //    (JsPath \ "confirmedPassword").read[String]
  //  ) (UserDto.apply _)

//  val confirmPassRead = new Reads[UserDto] {
//
//    def reads(jsv: JsValue): JsResult[UserDto] = {
//      userDtoReads.reads(jsv).flatMap { userDto =>
//        checkConfirmedPassword(userDto)
//      }
//    }
//
//    private def checkConfirmedPassword(userDto: UserDto): JsResult[UserDto] = {
//      val password = userDto.password
//      val confirmedPassword = userDto.confirmedPassword
//      if (password.equals(confirmedPassword)) {
//        JsSuccess(userDto)
//      } else {
//        JsError("Passwords must match")
//      }
//
//    }
//  }

}
