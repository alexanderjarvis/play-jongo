package uk.co.panaxiom.playjongo;

import java.lang.reflect.Constructor;

import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;

import play.Configuration;
import play.Logger;
import play.Play;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;

public class PlayJongo {

    private static volatile PlayJongo INSTANCE = null;

    MongoClient mongo = null;
    Jongo jongo = null;
    GridFS gridfs = null;

    PlayJongo(Configuration config, ClassLoader classLoader, boolean isTestMode) throws Exception {
        
        String clientFactoryName = config.getString("playjongo.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of "+ factory.getClass().getName());
        }

        DB db = mongo.getDB(factory.getDBName());

        jongo = new Jongo(db, createMapper(config, classLoader));

        if (config.getBoolean("playjongo.gridfs.enabled", false)) {
            gridfs = new GridFS(jongo.getDatabase());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected MongoClientFactory getMongoClientFactory(String className, Configuration config, boolean isTestMode) throws Exception {

        if (className != null) {
            try {
                Class factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                    throw new IllegalStateException("mongoClientFactory '" + className +
                            "' is not of type " + MongoClientFactory.class.getName());
                }

                Constructor constructor = null;
                try {
                    constructor = factoryClass.getConstructor(Configuration.class);
                } catch (Exception e) {
                    // can't use that one
                }
                if (constructor == null) {
                    return (MongoClientFactory) factoryClass.newInstance();
                }
                return (MongoClientFactory) constructor.newInstance(config);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        return new MongoClientFactory(config, isTestMode);
    }

    private Mapper createMapper(Configuration config, ClassLoader classLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String factoryClassName = config.getString("playjongo.mapperfactory",
                JongoMapperFactory.DefaultFactory.class.getName());
        JongoMapperFactory factory = (JongoMapperFactory) Class.forName(
                factoryClassName,
                true,
                classLoader).newInstance();
        return factory.create();
    }

    public static PlayJongo getInstance() {
        if (INSTANCE == null) {
            synchronized (PlayJongo.class) {
                if (INSTANCE == null) {
                    try {
                        INSTANCE = new PlayJongo(Play.application().configuration(), Play.application().classloader(), Play.isTest());
                    } catch (Exception e) {
                        Logger.error(e.getClass().getSimpleName(), e);
                    }
                }
            }
        }
        return INSTANCE;
    }

    public static void forceNewInstance() {
        INSTANCE = null;
        getInstance();
    }

    public static Mongo mongo() {
        return getInstance().mongo;
    }

    public static Jongo jongo() {
        return getInstance().jongo;
    }

    public static GridFS gridfs() {
        return getInstance().gridfs;
    }

    public static MongoCollection getCollection(String name) {
        return getInstance().jongo.getCollection(name);
    }

    public static DB getDatabase() {
        return getInstance().jongo.getDatabase();
    }
}