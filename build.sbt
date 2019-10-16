name := "sgit"
version := "0.1"
scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  // Test framework
  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  // File library
  "com.github.pathikrit" %% "better-files" % "3.8.0",
  // Command line parser
  "com.github.scopt" %% "scopt" % "4.0.0-RC2"
)

// Assembly for .jar
import sbtassembly.AssemblyPlugin.defaultUniversalScript
assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultUniversalScript(shebang = false)))
assemblyJarName in assembly := s"${name.value}-${version.value}"

parallelExecution in Test := false