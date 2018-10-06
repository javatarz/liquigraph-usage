package me.karun.migration.graph.neo4j

case class Neo4JAddress(uri: String) {
  def isBolt: Boolean = uri.startsWith("bolt://")
}
