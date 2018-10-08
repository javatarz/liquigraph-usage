package me.karun.migration.graph

import java.nio.file.{Path, Paths}

import me.karun.migration.graph.ApplicationTest.{RichPath, RichString}
import org.scalatest.WordSpec

class ApplicationTest extends WordSpec {
  "application" ignore { // Requires a database to be running. Need to find an alternative in memory DB.
    "invoked through a test" should {
      "validate the changelogs" in {
        val path = "target/test-graph".toPath
        path.mkdirs()

        GraphMigration().validate(path)
      }
    }
  }
}

object ApplicationTest {

  implicit class RichString(str: String) {
    def toPath: Path = Paths.get(str)
  }

  implicit class RichPath(path: Path) {
    def mkdirs(): Boolean = {
      path.toFile.mkdirs()
    }
  }
}
