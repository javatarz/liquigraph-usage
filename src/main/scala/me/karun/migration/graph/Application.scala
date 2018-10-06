package me.karun.migration.graph

import com.typesafe.config.ConfigFactory
import org.liquigraph.core.api.Liquigraph
import org.liquigraph.core.configuration.ConfigurationBuilder;

object Application extends App {

  private val config = ConfigFactory.load()
  private val url = config.getString("neo4j.url").split(",").map(_.trim).head
  private val username = config.getString("neo4j.username")
  private val password = config.getString("neo4j.password")

  private val configuration = new ConfigurationBuilder()
    .withMasterChangelogLocation("changelog.xml")
    .withUri(s"jdbc:neo4j:$url")
    .withUsername(username)
    .withPassword(password)
    .withRunMode()
    .build()

  new Liquigraph().runMigrations(configuration)
}
