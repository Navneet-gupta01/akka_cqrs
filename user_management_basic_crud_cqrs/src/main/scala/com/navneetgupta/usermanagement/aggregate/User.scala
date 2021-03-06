package com.navneetgupta.usermanagement.aggregate

import java.util.Date
import com.navneetgupta.cqrs.shared.fo.BaseFieldsObject
import com.navneetgupta.cqrs.shared.entity.BasePersistentEntity
import akka.actor.Props
import com.navneetgupta.usermanagement.command._
import com.navneetgupta.usermanagement.events._
import com.navneetgupta.cqrs.shared.event.BaseEvent
import com.navneetgupta.cqrs.shared.entity.BasePersistentEntity.MarkAsDeleted

case class UserFO(email: String, firstName: String, lastName: String,
                  createTs: Date, modifyTs: Date, deleted: Boolean = false) extends BaseFieldsObject[String, UserFO] {
  def assignId(id: String) = this.copy(email = id)
  def id = email
  def markDeleted = this.copy(deleted = true)
}
object UserFO {
  def empty = UserFO("", "", "", new Date(0), new Date(0))
}

object User {
  val EntityType = "user"

  def props = Props[User]
}
class User extends BasePersistentEntity[UserFO] {
  def initialState = UserFO.empty

  override def additionalCommandHandling = {
    case CreateUser(user) =>
      persist(UserCreated(user)) { handleEventAndRespond() }

    case UpdatePersonalInfo(input, id) =>
      persist(PersonalInfoUpdated(input.firstName, input.lastName)) { handleEventAndRespond() }
    case MarkAsDeleted(email) =>
      persist(UserDeleted(email))(handleEventAndRespond(false))
  }

  def handleEvent(event: BaseEvent) = event match {
    case UserCreated(user) =>
      state = user
    case PersonalInfoUpdated(first, last) =>
      state = state.copy(firstName = first, lastName = last)
    case UserDeleted(email) =>
      state = state.markDeleted
  }

  def isCreateMessage(cmd: Any) = cmd match {
    case cr: CreateUser => true
    case _              => false
  }

  override def newDeleteEvent = {
    log.info("===========================================new Delete Event===========")
    log.info("id is : {}", id)
    Some(UserDeleted(id))
  }

  override def snapshotAfterCount = Some(5)
}
