import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "user_management_basic_crud_cqrs",
    libraryDependencies  ++= {  
	  Seq(
	    "com.navneetgupta" %% "shared" % "0.1.1"
	  )
	}
  )
