PlayJongo Play 2.5.x Module
=====================================

This is a Play 2.5.x Module for [Jongo](http://jongo.org/)
(a MongoDB Java driver wrapper).

*If you're using Play! 2.4.x, 2.3.x, 2.2.x or 2.1.x you can use an older version of play-jongo:*
* for 2.4.x use `1.0.1-jongo1.2`
* for 2.3.x use `0.7.1-jongo1.0`
* for 2.2.x use `0.6.0-jongo1.0`
* for 2.1.x use `0.5.0-jongo0.4`

[![Build Status](https://jenkins.inoio.de/job/play-jongo/badge/icon)](http://jenkins.inoio.de/job/play-jongo/)

Installation
-----------

Add the following to your projects Build.scala (for build.sbt use `libraryDependencies` instead of `appDependencies`):

	val appDependencies = Seq(
	  "uk.co.panaxiom" %% "play-jongo" % "2.0.0-jongo1.3"
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

**Play Framework 2.5.x**

A way to use PlayJongo is to create a repositories package containing repository classes, one for each model. A repository class contains the PlayJongo injection, the collection to use and all methods to access to the collection members.
The package structure should be similar to the following:
```
|- controllers
|- models
|- repositories
```

Model example:

```java
public class Users  {

    @JsonProperty("_id")
    private ObjectId _id;

    private String firstname;

    private String lastname;

    private String email;

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId _id) {
        this._id = _id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

Repository example:

```java
public class UsersRepository {

    @Inject
    public PlayJongo jongo;
 
    public MongoCollection users() {
        return jongo.getCollection("DB.users");
    }
   
    public Users findById(String id) {
    	return users().findOne("{_id: #}", new ObjectId(id)).as(Users.class);
    }
}
```

Controller example:

```java
public class User extends Controller {

    @Inject
    private UsersRepository users;

    public Result modifyUser(String id) {
        Users u = users.findById(id);
        return ok(modifyUser.render(u));
    }
}
```


**Play Framework 2.x.x**

To use this module you just use the PlayJongo class which manages your Mongo and Jongo instances for you. It provides the same method calls as the Jongo object as detailed in the Jongo documentation: http://jongo.org/ .

If you are using `1.0.1-jongo1.2` and higher you have to inject PlayJongo:

A simple example:

```java
public class User {

    public static PlayJongo jongo = Play.application().injector().instanceOf(PlayJongo.class);

    public static MongoCollection users() {
        return jongo.getCollection("users");
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

If you are using any other jongo version, you should implement your POJOs this way:

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
