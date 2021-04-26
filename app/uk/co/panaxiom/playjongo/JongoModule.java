package uk.co.panaxiom.playjongo;

import java.util.Collections;

/**
 * Play 2.7 Module implementation.
 */
public class JongoModule extends play.inject.Module {

    @Override
    public java.util.List<play.inject.Binding<?>> bindings(final play.Environment environment, final com.typesafe.config.Config config) {
        return Collections.singletonList(bindClass(JongoModule.class).toSelf());
    }

}