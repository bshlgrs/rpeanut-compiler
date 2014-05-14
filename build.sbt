import com.typesafe.startscript.StartScriptPlugin

seq(StartScriptPlugin.startScriptForClassesSettings: _*)

name := "rpeanut_compiler"

version := "1.0"

scalaVersion := "2.9.2"

// resolvers += "twitter-repo" at "http://maven.twttr.com"

// libraryDependencies ++= Seq("com.twitter" % "finagle-core" % "1.9.0", "com.twitter" % "finagle-http" % "1.9.0")

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"
