package uk.co.panaxiom.playjongo;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.Configuration;

import static uk.co.panaxiom.playjongo.PlayJongoTest.MapBuilder.*;

import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.typesafe.config.ConfigFactory;

public class PlayJongoTest {
    
    @Test
    public void testMongoUriConfig() throws Exception {
        Map<String, String> config = mapBuilder("playjongo.uri", "mongodb://localhost:27017/foo").get();
        final PlayJongo cut = playJongo(config, false);
        
        assertMongoProperties(cut, "localhost", 27017, "foo");
    }
    
    @Test
    public void testMongoTestUriConfig() throws Exception {
        Map<String, String> config = mapBuilder("playjongo.test-uri", "mongodb://localhost:27017/bar").get();
        final PlayJongo cut = playJongo(config, true);

        assertMongoProperties(cut, "localhost", 27017, "bar");
    }
    
    @Test
    public void testMongoWriteConcern() throws Exception {
        Map<String, String> config = mapBuilder("playjongo.uri", "mongodb://localhost:27017/foo")
                .with("playjongo.defaultWriteConcern", "REPLICAS_SAFE").get();
        final PlayJongo cut = playJongo(config, false);

        assertThat(cut.jongo.getDatabase().getWriteConcern()).isEqualTo(WriteConcern.REPLICAS_SAFE);
    }

    private void assertMongoProperties(final PlayJongo cut, String host, int port, String dbName) {
        assertThat(cut.mongo.getServerAddressList().size()).isEqualTo(1);
        final ServerAddress server0 = cut.mongo.getServerAddressList().get(0);
        assertThat(server0.getHost()).isEqualTo(host);
        assertThat(server0.getPort()).isEqualTo(port);
        assertThat(cut.jongo.getDatabase().getName()).isEqualTo(dbName);
    }
    
    private static PlayJongo playJongo(Map<String, ? extends Object> config, boolean isTest) throws Exception {
        return new PlayJongo(new Configuration(ConfigFactory.parseMap(config)), classLoader(), isTest);
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    static class MapBuilder<K, V> {
        private final Map<K, V> m = new HashMap<K, V>();
        public MapBuilder(K key, V value) {
            m.put(key, value);
        }
        public static <K, V> MapBuilder<K, V> mapBuilder(K key, V value) {
            return new MapBuilder<K, V>(key, value);
        }
        public MapBuilder<K, V> with(K key, V value) {
            m.put(key, value);
            return this;
        }
        public Map<K, V> get() {
            return m;
        }
    }

}
