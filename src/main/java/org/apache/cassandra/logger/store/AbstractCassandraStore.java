package org.apache.cassandra.logger.store;

import com.datastax.driver.core.*;

public class AbstractCassandraStore {
    
    private String node;
    private Cluster cluster;
    private Session session;
    private String keyspace;
    private String table;

    protected AbstractCassandraStore(String node, String keyspace, String table) {
        this.node = node;
        this.keyspace = keyspace;
        this.table = table;
    }
    
    protected Cluster getCluster() {
        if (cluster == null) {
            cluster = Cluster.builder()
                    .addContactPoint(node)
                    .build();
        }
        return cluster;
    }

    protected Session getSession() {
        if (session == null) {
            session = getCluster().connect();
        }
        return session;
    }

    protected ResultSet execute(Statement statement) {
        return getSession().execute(statement);
    }
    
    public String getKeyspace() {
        return keyspace;
    }

    public String getTable() {
        return table;
    }
}