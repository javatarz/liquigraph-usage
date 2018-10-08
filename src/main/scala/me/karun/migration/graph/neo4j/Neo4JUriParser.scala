package me.karun.migration.graph.neo4j

import com.typesafe.scalalogging.LazyLogging
import org.neo4j.driver.v1.{AuthToken, Driver, GraphDatabase, Transaction}

import scala.collection.JavaConverters.asScalaBufferConverter

case class Neo4JUriParser(private val uri: String, private val authToken: AuthToken) extends LazyLogging {
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
    var driver: Option[Driver] = None
    var transaction: Option[Transaction] = None

    override def isDefinedAt(uri: String): Boolean = {
      try {
        driver = Some(GraphDatabase.driver(uri, authToken))
        transaction = Some(driver.get.session().beginTransaction())
        true
      } catch {
        case t: Throwable =>
          logger.warn(s"Unable to connect to $uri: ${t.getMessage}")

          closeQuietly(transaction)
          closeQuietly(driver)
          false
      }
    }

    override def apply(uri: String): Option[Neo4JAddress] = {
      val result = transaction.get.run("call dbms.cluster.overview")
        .list().asScala
        .map(Neo4JClusterOverview(_))
        .find(_.isLeader)
        .flatMap(_.boltAddress)

      closeQuietly(transaction)
      closeQuietly(driver)

      result
    }

    private def closeQuietly(closeable: Option[AutoCloseable]): Unit = {
      if (closeable.isDefined) closeable.get.close()
    }
  }
}
