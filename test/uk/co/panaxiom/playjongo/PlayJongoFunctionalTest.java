package uk.co.panaxiom.playjongo;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.*;

import play.api.Play;
import play.test.FakeApplication;
import play.test.WithApplication;

import static play.test.Helpers.*;

/**
 * Functional tests, with a running fake app.
 */
public class PlayJongoFunctionalTest extends WithApplication {

    private static final int PORT = 3333;

    @Test
    public void testInjection() throws Exception {
        PlayJongo jongo = app.injector().instanceOf(PlayJongo.class);
        assertThat(jongo).isNotNull();
    }


}
