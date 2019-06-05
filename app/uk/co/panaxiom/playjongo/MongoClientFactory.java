package uk.co.panaxiom.playjongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;

/**
 * The default MongoClient factory class for PlayJongo.
 *
 * @author ssayles
 */
public class MongoClientFactory {

    protected Config config;
    protected boolean isTest;

    public MongoClientFactory(Config config) {
        this.config = config;
    }

    protected MongoClientFactory(Config config, boolean isTest) {
        this.config = config;
        this.isTest = isTest;
    }

    /**
     * Creates and returns a new instance of a MongoClient.
     *
     * @return a new MongoClient
     */
    public MongoClient createClient() {
        MongoClientURI uri = getClientURI();

        MongoClient mongo = new MongoClient(uri);

        MongoDatabase db = mongo.getDatabase(uri.getDatabase());

        // Set write concern if configured
        if (config.hasPath("playjongo.defaultWriteConcern")) {
            String defaultWriteConcern = config.getString("playjongo.defaultWriteConcern");
            db.withWriteConcern(WriteConcern.valueOf(defaultWriteConcern));
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
                    ? config.getString("playjongo.test-uri")
                    : config.getString("playjongo.uri"));
        return uri;
    }
}
