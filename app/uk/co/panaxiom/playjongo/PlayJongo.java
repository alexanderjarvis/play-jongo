package uk.co.panaxiom.playjongo;

import java.net.UnknownHostException;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import play.Logger;
import play.Play;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;

public class PlayJongo {

	private static volatile PlayJongo INSTANCE = null;

	private Mongo mongo;
	private Jongo jongo;
	private GridFS gridfs;
	
	private PlayJongo() throws UnknownHostException, MongoException {
			mongo = new Mongo(Play.application().configuration().getString("playjongo.host"), 
					Play.application().configuration().getInt("playjongo.port"));
			jongo = new Jongo(mongo.getDB(Play.application().configuration().getString("playjongo.db")));
			gridfs = new GridFS(jongo.getDatabase());
	}
	
	public static PlayJongo getInstance() {
		if (INSTANCE == null) {
			synchronized (PlayJongo.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = new PlayJongo();
					} catch (UnknownHostException e) {
						Logger.error("UnknownHostException", e);
					} catch (MongoException e) {
						Logger.error("MongoException", e);
					}
				}
			}
		}
		return INSTANCE;
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
