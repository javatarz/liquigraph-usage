# Liquigraph Usage

Setup to perform migrations on a Neo4J Causal Cluster.

This project shows how to:
1. Accept multiple URLs as input to avoid failure in case a single node is down. This should be filled using a _Resource Manager_/Infrstructure as Code component like Chef.
1. Find the leader in a causal cluster
1. Accept a single URL for non clustered environments

## How to use

1. `docker-compose up` to start a 5 node cluster (4 cores, 1 read replica)
1. Run Application.scala

### Open Issues
#### Test migrations on commit
`ApplicationTest.scala` tries to test the changelogs but requires a Neo4J to do so.

Possible fix includes using an in-memory graph to validate the structure of the migrations.
