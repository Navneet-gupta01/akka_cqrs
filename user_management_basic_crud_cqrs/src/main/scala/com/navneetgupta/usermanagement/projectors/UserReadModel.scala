package com.navneetgupta.usermanagement.projectors

import com.navneetgupta.usermanagement.aggregate.User
import java.util.Date
import com.navneetgupta.cqrs.shared.view.ReadModelObject
import akka.actor.Props
import com.navneetgupta.cqrs.shared.view.ViewBuilder
import com.navneetgupta.usermanagement.protocols.UserJsonProtocol
import com.navneetgupta.usermanagement.events._
import akka.persistence.query.EventEnvelope
import com.navneetgupta.cqrs.shared.actor.BaseActor
import com.navneetgupta.cqrs.shared.readstore.ElasticsearchSupport
import akka.stream.ActorMaterializer

trait UserReadModel {
  def indexRoot = "usersrm"
  def entityType = User.EntityType
}
object UserViewBuilder {
  val Name = "user-view-builder"
  case class UserRM(email: String, firstName: String, lastName: String,
                    createTs: Date, deleted: Boolean = false) extends ReadModelObject {
    override def id = email
  }
  def props = Props[UserViewBuilder]
}
class UserViewBuilder extends ViewBuilder[UserViewBuilder.UserRM] with UserReadModel with UserJsonProtocol {
  import ViewBuilder._
  import UserViewBuilder._

  implicit override val rmFormats = userRmFormat

  override def projectionId = Name

  override def actionFor(id: String, env: EventEnvelope) = env.event match {
    case UserCreated(user) =>
      val rm = UserRM(user.email, user.firstName, user.lastName, user.createTs, user.deleted)
      InsertAction(id, rm)

    case PersonalInfoUpdated(first, last) =>
      UpdateAction(id, List("firstName = fn", "lastName = ln"), Map("fn" -> first, "ln" -> last))

    case UserDeleted(email) =>
      UpdateAction(id, "deleted = true", Map.empty[String, Any])
  }
}

object UserView {
  val Name = "user-view"
  case class FindUsersByName(name: String)
  def props = Props[UserView]
}

class UserView extends BaseActor with ElasticsearchSupport with UserReadModel with UserJsonProtocol {
  import UserView._
  import UserViewBuilder.UserRM
  import context.dispatcher

  implicit val mater = ActorMaterializer()

  def receive = {
    case FindUsersByName(name) =>
      val results = queryElasticsearch[UserRM](s"firstName:$name OR lastName:$name")
      pipeResponse(results)
  }
}
