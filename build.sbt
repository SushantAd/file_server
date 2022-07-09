lazy val akkaHttpVersion = "10.2.9"
lazy val akkaVersion    = "2.6.19"

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.demo.file.server",
      scalaVersion    := "2.13.4"
    )),
    name := "file-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,

      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5",
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test
    )
  )