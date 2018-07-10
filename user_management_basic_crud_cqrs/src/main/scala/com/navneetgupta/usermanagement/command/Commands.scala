package com.navneetgupta.usermanagement.command

import com.navneetgupta.usermanagement.aggregate.UserFO
import com.navneetgupta.cqrs.shared.command.BaseCommand

case class UserInput(firstName: String, lastName: String)

case class CreateUser(user: UserFO) extends BaseCommand {
  override def entityId = user.email
}
case class UpdatePersonalInfo(input: UserInput, id: String) extends BaseCommand {
  override def entityId = id
}
