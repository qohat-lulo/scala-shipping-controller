import Dependencies._

lazy val options = Seq(
  "-language:postfixOps",
  "-language:higherKinds",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Ymacro-annotations" // for newtype and simulacrum
)

lazy val commonSettings = Seq(
  name := "shipping-controller-test",
  version := "0.1",
  organization := "com.qohat",
  scalaVersion := "2.13.3",
  scalacOptions := options,
  scalaSource in Test := baseDirectory.value / "src/test/scala",
  scalaSource in IntegrationTest := baseDirectory.value / "src/it/scala",
  scalafmtOnCompile in ThisBuild := true,
  autoCompilerPlugins in ThisBuild := true,
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", _ @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .configs(Test)
  .settings(inConfig(Test)(Defaults.testSettings))
  .settings(
    libraryDependencies ++= Seq(
      cats("cats-macros"),
      cats("cats-kernel"),
      cats("cats-core"),
      cats("cats-effect"),
      fs2("fs2-core"),
      fs2("fs2-io"),
      logback("logback-classic"),
      log4cats("log4cats-core"),
      log4cats("log4cats-slf4j"),
      scalaTest % Test
    ),
    mainClass in assembly := Some("com.qohat.main.Main"),
    assemblyJarName in assembly := "main.jar"
  ).aggregate(infrastructure, core, test, shipping)
  .dependsOn(
    shipping_domain,
    shipping_adapter,
    shipping_application,
    core,
    test,
    infra_files
  )

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    scalacOptions ++= options,
    libraryDependencies += newType
  ).dependsOn(test)

lazy val shipping = (project in file("shipping-controller"))
  .settings(commonSettings: _*)
  .settings(
    name := "shipping-controller"
  ).aggregate(
  shipping_domain,
  shipping_adapter,
  shipping_application
)

lazy val shipping_domain = (project in file("shipping-controller/domain"))
  .settings(commonSettings: _*)
  .settings(
    name := "shipping-controller-domain",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      fs2("fs2-core"),
      scalaTest % Test
    )
  ).dependsOn(core, test)

lazy val shipping_adapter = (project in file("shipping-controller/adapter"))
  .settings(commonSettings: _*)
  .settings(
    name := "shipping-controller-adapter",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      cats("cats-macros"),
      cats("cats-kernel"),
      cats("cats-core"),
      cats("cats-effect"),
      fs2("fs2-core"),
      refined("refined"),
      refined("refined-cats"),
      ciris("ciris"),
      ciris("ciris-refined"),
      ciris("ciris-enumeratum"),
      simulacrum,
      scalaTest % Test
    )
  ).dependsOn(core, test, infra_files, shipping_domain)

lazy val shipping_application = (project in file("shipping-controller/application"))
  .settings(commonSettings: _*)
  .settings(
    name := "shipping-controller-application",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      cats("cats-macros"),
      cats("cats-kernel"),
      cats("cats-core"),
      cats("cats-effect"),
      fs2("fs2-core"),
      refined("refined"),
      refined("refined-cats"),
      ciris("ciris"),
      ciris("ciris-refined"),
      ciris("ciris-enumeratum"),
      simulacrum,
      scalaTest % Test
    )
  ).dependsOn(
  core, test, shipping_domain
)

lazy val test = (project in file("test"))
  .settings(commonSettings: _*)
  .settings(
    name := "test",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      cats("cats-macros"),
      cats("cats-kernel"),
      cats("cats-core"),
      cats("cats-effect"),
      fs2("fs2-core"),
      scalaTest
    )
  )

lazy val infrastructure = (project in file("infra"))
  .settings(commonSettings: _*)
  .settings(name := "infra")
  .aggregate(infra_files)

lazy val infra_files = (project in file("infra/files"))
  .settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings))
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(
    name := "files",
    scalacOptions ++= options,
    libraryDependencies ++= Seq(
      cats("cats-macros"),
      cats("cats-kernel"),
      cats("cats-core"),
      cats("cats-effect"),
      fs2("fs2-core"),
      fs2("fs2-io"),
      logback("logback-classic"),
      log4cats("log4cats-core"),
      log4cats("log4cats-slf4j"),
      simulacrum,
      scalaTest % Test
    )
  ).dependsOn(test)
