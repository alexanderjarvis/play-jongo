package uk.co.panaxiom.playjongo;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.typesafe.config.Config;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import play.Environment;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

@Singleton
public class PlayJongo {

    MongoClient mongo = null;
    Jongo jongo = null;
    GridFS gridfs = null;


    @Inject
    public PlayJongo(ApplicationLifecycle lifecycle, Environment env, Config config) {

        try {
            configure(config, env.classLoader(), env.isTest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lifecycle.addStopHook(()->{
            if (env.isTest()) {
                mongo().close();
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    PlayJongo(Config config, ClassLoader classLoader, boolean isTestMode) throws Exception {
        configure(config,classLoader,isTestMode);
    }

    private void configure(Config config, ClassLoader classLoader, boolean isTestMode) throws Exception {
        
        String clientFactoryName = config.getString("playjongo.mongoClientFactory");
        MongoClientFactory factory = getMongoClientFactory(clientFactoryName, config, isTestMode);
        mongo = factory.createClient();

        if (mongo == null) {
            throw new IllegalStateException("No MongoClient was created by instance of "+ factory.getClass().getName());
        }

        DB db = mongo.getDB(factory.getDBName());

        jongo = new Jongo(db, createMapper(config, classLoader));

        if (config.getBoolean("playjongo.gridfs.enabled")) {
            gridfs = new GridFS(jongo.getDatabase());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected MongoClientFactory getMongoClientFactory(String className, Config config, boolean isTestMode) throws Exception {

        if (!className.equals("uk.co.panaxiom.playjongo.MongoClientFactory")) {
            try {
                Class factoryClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                if (!MongoClientFactory.class.isAssignableFrom(factoryClass)) {
                    throw new IllegalStateException("mongoClientFactory '" + className +
                            "' is not of type " + MongoClientFactory.class.getName());
                }

                Constructor constructor = null;
                try {
                    constructor = factoryClass.getConstructor(Config.class);
                } catch (Exception e) {
                    // can't use that one
                }
                if (constructor == null) {
                    return (MongoClientFactory) factoryClass.getDeclaredConstructor().newInstance();
                }
                return (MongoClientFactory) constructor.newInstance(config);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        return new MongoClientFactory(config, isTestMode);
    }

    private Mapper createMapper(Config config, ClassLoader classLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String factoryClassName = config.getString("playjongo.mapperfactory");
        JongoMapperFactory factory = (JongoMapperFactory) Class.forName(
                factoryClassName,
                true,
                classLoader).newInstance();
        return factory.create();
    }


    public Mongo mongo() {
        return mongo;
    }

    public Jongo jongo() {
        return jongo;
    }

    public GridFS gridfs() {
        return gridfs;
    }

    public MongoCollection getCollection(String name) {
        return jongo.getCollection(name);
    }

    public DB getDatabase() {
        return jongo.getDatabase();
    }
}