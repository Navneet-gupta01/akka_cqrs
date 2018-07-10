package com.navneetgupta.usermanagement

import com.navneetgupta.cqrs.shared.server.Server

object Main extends App {
  new Server(new UserBoot(), "user-management1")
}
