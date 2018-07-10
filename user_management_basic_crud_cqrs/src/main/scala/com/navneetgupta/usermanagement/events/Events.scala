package com.navneetgupta.usermanagement.events

import com.navneetgupta.cqrs.shared.event.BaseEvent
import com.navneetgupta.usermanagement.aggregate.User
import com.navneetgupta.usermanagement.aggregate.UserFO
import com.navneetgupta.usermanagement.protobuf.Datamodel
import com.navneetgupta.usermanagement.protobuf.Datamodel._
import com.navneetgupta.cqrs.shared.adapter.DatamodelReader
import java.util.Date

trait UserEvent extends BaseEvent { override def entityType = User.EntityType }

object UserCreated extends DatamodelReader {
  override def fromDatamodel = {
    case dm: Datamodel.UserCreated =>
      println("From DataModel UserCreated")
      val user = dm.getUser()
      UserCreated(UserFO(user.getEmail(), user.getFirstName(), user.getLastName(), new Date(user.getCreateTs()), new Date(user.getModifyTs()), user.getDeleted()))
  }
}

case class UserCreated(user: UserFO) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel UserCreated")
    val userDm = Datamodel.User.newBuilder().
      setEmail(user.email).
      setFirstName(user.firstName).
      setLastName(user.lastName).
      setCreateTs(new Date().getTime).
      setModifyTs(new Date().getTime).
      setDeleted(user.deleted)
      .build

    Datamodel.UserCreated.newBuilder().
      setUser(userDm).
      build
  }
}

case class PersonalInfoUpdated(firstName: String, lastName: String) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel PersonalInfoUpdated")
    Datamodel.PersonalInfoUpdated.newBuilder().
      setFirstName(firstName).
      setLastName(lastName).
      setModifyTs(new Date().getTime).
      build
  }
}

object PersonalInfoUpdated extends DatamodelReader {
  override def fromDatamodel = {
    case dm: Datamodel.PersonalInfoUpdated =>
      println("From DataModel PersonalInfoUpdated")
      PersonalInfoUpdated(dm.getFirstName(), dm.getLastName())
  }
}

case class UserDeleted(email: String) extends UserEvent {
  override def toDatamodel = {
    println("To DataModel UserDeleted")
    Datamodel.UserDeleted.newBuilder().
      setEmail(email).
      setModifyTs(new Date().getTime). // Not needed since events are alreay there for the time at which they are called
      build
  }
}

object UserDeleted extends DatamodelReader {
  override def fromDatamodel = {
    case dm: Datamodel.UserDeleted =>
      println("From DataModel UserDeleted")
      UserDeleted(dm.getEmail())
  }
}
