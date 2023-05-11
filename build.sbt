ThisBuild / scalaVersion := "3.3.1-RC1-bin-SNAPSHOT"

lazy val model = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("model"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "3.0.0",
      "org.scalameta" %%% "munit" % "1.0.0-M7" % Test
    )
  )

lazy val webpageDom = project
  .in(file("webpage-dom"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0"
    )
  )
  .dependsOn(model.js)

lazy val webpageClient = project
  .in(file("webpage-client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0"
    )
  )
  .dependsOn(model.js)

lazy val webpage = project
  .in(file("webpage"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.4.0"
    )
  )
  .dependsOn(webpageClient, webpageDom)

lazy val caskExtensions = project
  .in(file("cask-extensions"))
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "cask" % "0.9.0",
    ),
  )

lazy val webserverRepo = project
  .in(file("webserver-repo"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0-M7" % Test
    ),
  )
  .dependsOn(model.jvm)

lazy val webserver = project
  .in(file("webserver"))
  .settings(
    Compile / resourceGenerators += Def.task {
      val source = (webpage / Compile / scalaJSLinkedFile).value.data
      val dest = (Compile / resourceManaged).value / "assets" / "main.js"
      IO.copy(Seq(source -> dest))
      Seq(dest)
    },
    run / fork := true
  )
  .dependsOn(webserverRepo, caskExtensions)
