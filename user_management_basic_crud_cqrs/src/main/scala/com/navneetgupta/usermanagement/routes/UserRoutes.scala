package com.navneetgupta.usermanagement.routes

import akka.actor.ActorRef
import scala.concurrent.ExecutionContext
import com.navneetgupta.cqrs.shared.rotues.BaseRouteDefination
import com.navneetgupta.usermanagement.protocols.UserJsonProtocol
import akka.stream.Materializer
import akka.http.scaladsl.server.Route
import akka.actor.ActorSystem
import com.navneetgupta.usermanagement.aggregate.UserFO
import com.navneetgupta.usermanagement.aggregate.UserAggregate.{ FindUserByEmail, UpdateUser, DeleteUser, SignUpUser, CreateUserInput }
import com.navneetgupta.usermanagement.command.UserInput
import com.navneetgupta.usermanagement.projectors.UserViewBuilder.UserRM
import com.navneetgupta.usermanagement.projectors.UserView.FindUsersByName
import com.navneetgupta.usermanagement.command.CreateUser

class UserRoutes(userAggregate: ActorRef, view: ActorRef)(implicit val ec: ExecutionContext) extends BaseRouteDefination with UserJsonProtocol {

  import akka.pattern._
  import akka.http.scaladsl.server.Directives._

  override def routes(implicit system: ActorSystem, ec: ExecutionContext, mater: Materializer): Route = {
    pathPrefix("user") {
      path(Segment) { email =>
        get {
          serviceAndComplete[UserFO](FindUserByEmail(email), userAggregate)
        } ~
          put {
            entity(as[UserInput]) { input =>
              serviceAndComplete[UserFO](UpdateUser(email, input), userAggregate)
            }
          } ~
          delete {
            serviceAndComplete[UserFO](DeleteUser(email), userAggregate)
          }
      } ~
        pathEndOrSingleSlash {
          (get & parameter('name)) { name =>
            serviceAndComplete[List[UserRM]](FindUsersByName(name), view)
          } ~
            post {
              entity(as[CreateUserInput]) { input =>
                serviceAndComplete[UserFO](SignUpUser(input), userAggregate)
              }
            }
        }
    }
  }
}
