package me.karun.migration.graph

import java.nio.file.Path

import com.typesafe.config.ConfigFactory
import me.karun.migration.graph.neo4j.Neo4JUriParser
import org.liquigraph.core.api.Liquigraph
import org.liquigraph.core.configuration.{Configuration, ConfigurationBuilder}
import org.neo4j.driver.v1.AuthTokens

case class GraphMigration(private val confFileName : Option[String] = None) {
  private val config = if (confFileName.isDefined) ConfigFactory.load(confFileName.get) else ConfigFactory.load()
  private val uris = config.getString("neo4j.url")
  private val username = config.getString("neo4j.username")
  private val password = config.getString("neo4j.password")

  private lazy val leaderUri = Neo4JUriParser(uris, AuthTokens.basic(username, password)).findLeader

  def execute(): Unit = {
    if (leaderUri.isEmpty) throw new RuntimeException("No leader found in the cluster")
    run(configBuilder.withRunMode().build())
  }

  def validate(outputDirectory: Path): Unit = {
    if (leaderUri.isEmpty) throw new RuntimeException("No leader found in the cluster")
    run(configBuilder.withDryRunMode(outputDirectory).build())
  }

  private def configBuilder = new ConfigurationBuilder()
    .withMasterChangelogLocation("changelog.xml")
    .withUri(s"jdbc:neo4j:${leaderUri.get}")
    .withUsername(username)
    .withPassword(password)

  private def run(configuration: Configuration): Unit = new Liquigraph().runMigrations(configuration)
}
