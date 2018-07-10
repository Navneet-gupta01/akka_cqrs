package com.navneetgupta.usermanagement.aggregate

import com.navneetgupta.usermanagement.command._
import akka.actor.Props
import com.navneetgupta.cqrs.shared.model.ErrorMessage
import com.navneetgupta.cqrs.shared.aggregate.BaseAggregate
import com.navneetgupta.cqrs.shared.entity.BasePersistentEntity.GetState
import akka.util.Timeout
import com.navneetgupta.cqrs.shared.model._
import java.util.Date
import com.navneetgupta.cqrs.shared.entity.BasePersistentEntity.MarkAsDeleted

object UserAggregate {
  val Name = "ua"

  //Lookup Operations
  case class FindUserByEmail(email: String)

  //Modify operations
  case class CreateUserInput(email: String, firstName: String, lastName: String)
  case class SignUpUser(input: CreateUserInput)
  case class UpdateUser(email: String, input: UserInput)
  case class DeleteUser(email: String)

  def props = Props[UserAggregate]

  val EmailNotUniqueError = ErrorMessage("user.email.nonunique", Some("The email supplied for a create or update is not unique"))
}
class UserAggregate extends BaseAggregate[UserFO, User] {
  import UserAggregate._
  import context.dispatcher
  import scala.concurrent.duration._
  import akka.pattern.ask
  import User._

  override def receive = {
    case FindUserByEmail(email) =>
      log.info("Finding User by email {}", email)

      forwardCommand(email, GetState(email))
    case SignUpUser(input) =>
      implicit val timeout = Timeout(5 seconds)
      val stateFut = (entityShardRegion ? GetState(input.email)).mapTo[ServiceResult[UserFO]]
      val caller = sender()
      stateFut onComplete {
        case util.Success(FullResult(user)) =>
          caller ! Failure(FailureType.Validation, EmailNotUniqueError)

        case util.Success(EmptyResult) =>
          val fo = UserFO(input.email, input.firstName, input.lastName, new Date, new Date)
          entityShardRegion.tell(CreateUser(fo), caller)

        case _ =>
          caller ! Failure(FailureType.Service, ServiceResult.UnexpectedFailure)
      }
    case UpdateUser(email, input) =>
      forwardCommand(email, UpdatePersonalInfo(input, email))

    case DeleteUser(email) =>
      forwardCommand(email, MarkAsDeleted)
  }

  override def entityProps: akka.actor.Props = User.props
}
