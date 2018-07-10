package com.navneetgupta.usermanagement.protocols

import com.navneetgupta.cqrs.shared.json.BaseJsonProtocol
import com.navneetgupta.usermanagement.aggregate.UserFO
import com.navneetgupta.usermanagement.projectors.UserViewBuilder.UserRM
import com.navneetgupta.usermanagement.command.UserInput
import com.navneetgupta.usermanagement.aggregate.UserAggregate.CreateUserInput

trait UserJsonProtocol extends BaseJsonProtocol {
  implicit val userFoFormat = jsonFormat6(UserFO.apply)
  implicit val userRmFormat = jsonFormat5(UserRM.apply)
  implicit val userInputFormat = jsonFormat2(UserInput.apply)
  implicit val createUserInputFormat = jsonFormat3(CreateUserInput.apply)

}
