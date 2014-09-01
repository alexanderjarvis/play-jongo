package uk.co.panaxiom.playjongo;

import play.Configuration;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;

/**
 * The default MongoClient factory class for PlayJongo.
 *
 * @author ssayles
 */
public class MongoClientFactory {

    protected Configuration config;
    protected boolean isTest;

    public MongoClientFactory(Configuration config) {
        this.config = config;
    }

    protected MongoClientFactory(Configuration config, boolean isTest) {
        this.config = config;
        this.isTest = isTest;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     * @throws Exception
     */
    public MongoClient createClient() throws Exception {
        MongoClientURI uri = getClientURI();

        MongoClient mongo = new MongoClient(uri);
        DB db = mongo.getDB(uri.getDatabase());

        // Set write concern if configured
        String defaultWriteConcern = config.getString("playjongo.defaultWriteConcern");
        if(defaultWriteConcern != null) {
            db.setWriteConcern(WriteConcern.valueOf(defaultWriteConcern));
        }
        
        return mongo;
    }

    /**
     * Returns the database name associated with the current configuration.
     *
     * @return The database name
     */
    public String getDBName() {
        return getClientURI().getDatabase();
    }

    protected MongoClientURI getClientURI() {
        MongoClientURI uri = new MongoClientURI(
                isTest
                    ? config.getString("playjongo.test-uri", "mongodb://127.0.0.1:27017/test")
                    : config.getString("playjongo.uri", "mongodb://127.0.0.1:27017/play"));
        return uri;
    }
}
