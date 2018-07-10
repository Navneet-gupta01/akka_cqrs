package com.navneetgupta.usermanagement

import com.navneetgupta.cqrs.shared.boot.Bootstrap
import akka.actor.ActorSystem
import com.navneetgupta.usermanagement.aggregate.UserAggregate
import com.navneetgupta.usermanagement.projectors.UserView
import com.navneetgupta.usermanagement.projectors.UserViewBuilder
import com.navneetgupta.usermanagement.routes.UserRoutes

class UserBoot extends Bootstrap {
  override def bootstrap(system: ActorSystem) = {
    import system.dispatcher
    val crm = system.actorOf(UserAggregate.props, UserAggregate.Name)
    val view = system.actorOf(UserView.props, UserView.Name)
    startSingleton(system, UserViewBuilder.props, UserViewBuilder.Name)
    List(new UserRoutes(crm, view))
  }
}
