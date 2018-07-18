package com.navneetgupta.usermanagement.events

import com.navneetgupta.cqrs.shared.event.BaseEvent
import com.navneetgupta.usermanagement.aggregate.User
import com.navneetgupta.usermanagement.aggregate.UserFO
import com.navneetgupta.usermanagement.proto.datamodel.{ UserCreated => ProtoUserCreated, User => ProtoUser, PersonalInfoUpdated => ProtoPersonalInfoUpdated, UserDeleted => ProtoUserDeleted }

import com.navneetgupta.cqrs.shared.adapter.DatamodelReader
import java.util.Date

trait UserEvent extends BaseEvent { override def entityType = User.EntityType }

case class UserCreated(user: UserFO) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel UserCreated")
    val userDm = ProtoUser.apply(
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      createTs = user.createTs.getTime,
      modifyTs = user.modifyTs.getTime,
      deleted = user.deleted)

    ProtoUserCreated(Some(userDm))
  }
}

case class PersonalInfoUpdated(firstName: String, lastName: String) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel PersonalInfoUpdated")
    ProtoPersonalInfoUpdated.apply(
      firstName = firstName,
      lastName = lastName,
      modifyTs = new Date().getTime)
  }
}
case class UserDeleted(email: String) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel UserDeleted")
    ProtoUserDeleted.apply(
      email = email,
      modifyTs = new Date().getTime)
  }
}

object UserCreated extends DatamodelReader {
  override def fromDatamodel = {
    case dm: ProtoUserCreated =>
      println("From DataModel UserCreated")
      val user = dm.user.get
      UserCreated(
        user = UserFO(user.email, user.firstName, user.lastName, new Date(user.createTs), new Date(user.modifyTs), user.deleted))
  }
}

object PersonalInfoUpdated extends DatamodelReader {
  override def fromDatamodel = {
    case dm: ProtoPersonalInfoUpdated =>
      println("From DataModel PersonalInfoUpdated")
      PersonalInfoUpdated(dm.firstName, dm.lastName)
  }
}

object UserDeleted extends DatamodelReader {
  override def fromDatamodel = {
    case dm: ProtoUserDeleted =>
      println("From DataModel UserDeleted")
      UserDeleted(dm.email)
  }
}
