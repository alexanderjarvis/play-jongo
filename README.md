PlayJongo Play 2.4.x Module
=====================================

This is a Play 2.4.x Module for [Jongo](http://jongo.org/)
(a MongoDB Java driver wrapper).

*If you're using Play! 2.3.x, 2.2.x or 2.1.x you can use an older version of play-jongo, for 2.3.x use `0.7.1-jongo1.0`, for 2.2.x use `0.6.0-jongo1.0`, for 2.1.x use `0.5.0-jongo0.4`.*

[![Build Status](https://jenkins.inoio.de/job/play-jongo/badge/icon)](http://jenkins.inoio.de/job/play-jongo/)

Installation
-----------

Add the following to your projects Build.scala

	val appDependencies = Seq(
	  "uk.co.panaxiom" %% "play-jongo" % "0.8.0-jongo1.2"
	)

*__Note related to play-jongo with Play 2.2:__ because there were issues reported due to incompatibilities of Play 2.2!, bson4jackson and the current version of jackson,
you might actually want to use the following dependencies:*

	val appDependencies = Seq(
	  "de.undercouch" % "bson4jackson" % "2.1.0" force(),
	  "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.0" force(),
	  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.0" force(),
	  "com.fasterxml.jackson.core" % "jackson-core" % "2.1.0" force(),
	  "org.mongodb" % "mongo-java-driver" % "2.11.3",
	  "org.jongo" % "jongo" % "1.0",
	  "uk.co.panaxiom" %% "play-jongo" % "0.7.0-jongo1.0"
	)

You will need to override the application.conf configuration to specify your MongoDB configuration.

	# Play Jongo
	# ~~~~~
	playjongo.uri="mongodb://127.0.0.1:27017/play"
	playjongo.gridfs.enabled=false

### Optional configuration

The default write concern for the mongodb driver can be changed via the following configuration:

	playjongo.defaultWriteConcern="SAFE"

Valid values are the names of the [WriteConcern enumeration](http://api.mongodb.org/java/current/com/mongodb/WriteConcern.html) fields.

By default in test mode the "test" mongo database will be used. To change this, you can specify `playjongo.test-uri` in your configuration.

#### MongoClient Factory

Not all MongoClient options are supported by the MongoClientURI.  For full customization of the generated MongoClient, you can specify
your own MongoClientFactory class like this:

    playjongo.mongoClientFactory="com.example.MyMongoClientFactory"

The value should be the name of a class that extends from `uk.co.panaxiom.MongoClientFactory` and provide at least an empty constructor or
a constructor that takes a play Configuration.  For example:

```java
package com.example;

import com.mongodb.*;
import java.util.Arrays;
import play.Configuration;
import uk.co.panaxiom.MongoClientFactory;

public class MyMongoClientFactory extends MongoClientFactory {
    private Configuration config = config;
    public MyMongoClientFactory(Configuration config) {
        this.config = config;
    }

    public MongoClient createClient() throws Exception {
        MongoClientOptions options = MongoClientOptions.builder()
            .connectionsPerHost(100)
            .maxConnectionIdleTime(60000)
            .build() 

        return new MongoClient(Arrays.asList(
            new ServerAddress("localhost", 27017),
            new ServerAddress("localhost", 27018),
            new ServerAddress("localhost", 27019)),
            options);
    }

    public String getDBName() {
        config.getString("myappconfig.dbname");
    }
    
}
```

#### Jongo Mapper

To customize the jongo mapper (see also ["Configuring Jongo Mapper"](http://jongo.org/#jongo-mapper) in the Jongo documentation) you can
provide a factory that will be used to obtain the mapper passed to jongo. Your factory class must implement
`uk.co.panaxiom.playjongo.JongoMapperFactory` and provide a default (public no-args) constructor.
You can configure your mapper factory like this:

    playjongo.mapperfactory="com.example.MyJongoMapperFactory"

For scala users this project already provides a factory that uses the [jackson scala module](https://github.com/FasterXML/jackson-module-scala)
and configures the jongo/jackson mapper with the `DefaultScalaModule`. To use this factory you need add the jackson scala module to your appDependencies
(e.g. add `"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.4.1"`) in `project/Build.scala` and configure the factory in
`conf/application.conf` like this:

    playjongo.mapperfactory="uk.co.panaxiom.playjongo.JongoScalaMapperFactory"

Usage
-----

To use this module you just use the PlayJongo class which manages your Mongo and Jongo instances for you. It provides the same method calls as the Jongo object as detailed in the Jongo documentation: http://jongo.org/ .

A simple example:

```java
public class User {

    public static MongoCollection users() {
        return PlayJongo.getCollection("users");
    }

    @JsonProperty("_id")
    public ObjectId id;

    public String name;

    public User insert() {
        users().save(this);
    }

    public void remove() {
        users().remove(this.id);
    }

    public static User findByName(String name) {
        return users().findOne("{name: #}", name).as(User.class);
    }

}
```

Contact
-------

If you have a question or need some help you can just [open an issue](https://github.com/alexanderjarvis/play-jongo/issues). 

License
-------

The license is Apache 2.0, see LICENSE.txt.
