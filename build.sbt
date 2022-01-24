import scala.sys.process.Process

enablePlugins(ScalaJSPlugin)

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.0"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .settings(
    name := "LaminarPresentation",
    // This is an application with a main method
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.14.2",
      "com.lihaoyi" %%% "upickle" % "1.4.3", // SBT
      "com.raquo" %%% "waypoint" % "0.5.0",
    )
  )

lazy val fastOptJSCopyToServer = taskKey[Unit]("Build JS application and then copy to Server static resources directory")
fastOptJSCopyToServer := {
  (root/Compile/fastOptJS).value
  Process("cp ./target/scala-3.1.0/laminarpresentation-fastopt.js ./src/main/resources/laminarpresentation-fastopt.js")!
}
