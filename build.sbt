name := "SuperMegaCRUD"
 
version := "1.0" 
      
lazy val `supermegacrud` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.1"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.3"

libraryDependencies += "com.h2database" % "h2" % "1.4.192"

libraryDependencies += evolutions
