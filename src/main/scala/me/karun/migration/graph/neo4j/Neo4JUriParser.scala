package me.karun.migration.graph.neo4j

import org.neo4j.driver.v1.{AuthToken, GraphDatabase}

import scala.collection.JavaConverters.asScalaBufferConverter

case class Neo4JUriParser(private val uri: String, private val authToken: AuthToken) {
  def findLeader: Option[String] = {
    val parts = uri.split(",")
    if (parts.size == 1) Some(if (uri.contains("://")) uri else s"bolt://$uri")
    else parts
      .map(s => if (s.contains("://")) s else s"bolt://$s")
      .collectFirst(pf)
      .get
      .map(_.uri)
  }

  private val pf: PartialFunction[String, Option[Neo4JAddress]] = new PartialFunction[String, Option[Neo4JAddress]] {
    override def isDefinedAt(x: String): Boolean = !x.isEmpty

    override def apply(uri: String): Option[Neo4JAddress] = {
      val driver = GraphDatabase.driver(uri, authToken)
      val transaction = driver.session().beginTransaction()

      val result = transaction.run("call dbms.cluster.overview")
        .list().asScala
        .map(Neo4JClusterOverview(_))
        .find(_.isLeader)
        .flatMap(_.boltAddress)

      transaction.close()
      driver.close()

      result
    }
  }
}
