name := "PacmanLogger"

version := "0.1.4"

scalaVersion := "2.12.4"

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps"
)

libraryDependencies ++= Seq(
  "com.googlecode.lanterna" % "lanterna" % "3.0.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.0"
)
