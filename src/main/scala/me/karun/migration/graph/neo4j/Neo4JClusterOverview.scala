package me.karun.migration.graph.neo4j

import org.neo4j.driver.internal.value.ListValue
import org.neo4j.driver.v1.Record

import scala.collection.JavaConverters.asScalaBufferConverter

case class Neo4JClusterOverview(private val addresses: Seq[Neo4JAddress], private val role: String) {
  def isLeader: Boolean = role == "LEADER"

  def boltAddress: Option[Neo4JAddress] = addresses.find(_.isBolt)
}

object Neo4JClusterOverview {
  def apply(r: Record): Neo4JClusterOverview = {
    val addresses = r.get("addresses").asInstanceOf[ListValue]
      .asList().asScala.toList
      .map(o => Neo4JAddress(o.toString))

    val role = r.get("role").asString()

    Neo4JClusterOverview(addresses, role)
  }
}
