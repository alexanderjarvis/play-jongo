package uk.co.panaxiom.playjongo;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.*;

import play.api.Play;
import play.test.FakeApplication;
import static play.test.Helpers.*;

/**
 * Functional tests, with a running fake app.
 */
public class PlayJongoFunctionalTest {

    private static final int PORT = 3333;
    private static FakeApplication app;

    @BeforeClass
    public static void setup() {
        try {
            app = fakeApplication();
            Play.start(app.getWrappedApplication());
        } catch (final RuntimeException e) {
            // If we throw this exception,
            // all tests in this class are skipped
            // without any error (in sbt).
            // This way they will at least fail.
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void stopApp() {
        Play.stop(app.getWrappedApplication());
    }

    @Test
    public void testInjection() throws Exception {
        PlayJongo jongo = app.injector().instanceOf(PlayJongo.class);
        assertThat(jongo).isNotNull();
    }


}
