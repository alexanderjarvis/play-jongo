PlayJongo Play 2.1 Module
=====================================

This is a Play 2.1 Module for [Jongo](http://jongo.org/)
(a MongoDB Java driver wrapper).

[![Build Status](https://jenkins.inoio.de/job/play-jongo/badge/icon)](http://jenkins.inoio.de/job/play-jongo/)

Installation
-----------

Add the following to your projects Build.scala

	val appDependencies = Seq(
	  "uk.co.panaxiom" %% "play-jongo" % "0.5.0-jongo0.4"
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

To customize the jongo mapper (see also ["Configuring Jongo Mapper"](http://jongo.org/#jongo-mapper) in the Jongo documentation) you can
provide a factory that will be used to obtain the mapper passed to jongo. Your factory class must implement
`uk.co.panaxiom.playjongo.JongoMapperFactory` and provide a default (public no-args) constructor.
You can configure your mapper factory like this:

	playjongo.mapperfactory="com.example.MyJongoMapperFactory"

For scala users this project already provides a factory that uses the [jackson scala module](https://github.com/FasterXML/jackson-module-scala)
and configures the jongo/jackson mapper with the `DefaultScalaModule`. To use this factory you need add the jackson scala module to your appDependencies
(e.g. add `"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.1"`) in `project/Build.scala` and configure the factory in
`conf/application.conf` like this:

	playjongo.mapperfactory="uk.co.panaxiom.playjongo.JongoScalaMapperFactory"

Usage
-----

To use this module you just use the PlayJongo class which manages your Mongo and Jongo instances for you. It provides the same method calls as the Jongo object as detailed in the Jongo documentation: http://jongo.org/ .

A simple example:

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

License
-------

The license is Apache 2.0, see LICENSE.txt.
