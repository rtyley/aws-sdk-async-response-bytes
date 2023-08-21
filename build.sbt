Compile / run / fork := true

// run / javaOptions += "-Xmx10m"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % "2.20.120",
  "software.amazon.awssdk" % "aws-crt-client" % "2.20.130",
  "org.slf4j" % "slf4j-simple" % "2.0.7",
  "com.google.guava" % "guava" % "32.1.2-jre"
)

commands += Command.single("runWithMaxMem") { (state, arg) =>
  s"""set run / javaOptions += "-Xmx${arg}m"""" :: "run" :: state
}

commands += Command.single("repeatRunWithMaxMem") { (state, arg) =>
   List.fill(20)(s"runWithMaxMem $arg") ::: state
}