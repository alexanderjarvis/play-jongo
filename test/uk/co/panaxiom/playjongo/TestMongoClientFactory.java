package uk.co.panaxiom.playjongo;

import com.typesafe.config.Config;

public class TestMongoClientFactory extends MongoClientFactory {

    public TestMongoClientFactory(Config config) {
        super(config);
    }

    public TestMongoClientFactory(Config config, boolean isTest) {
        super(config, isTest);
    }

    @Override
    public String getDBName() {
        return "testMongoClientFactory";
    }
}