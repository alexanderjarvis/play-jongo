package uk.co.panaxiom.playjongo;

import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;

import com.fasterxml.jackson.module.scala.*;

/**
 * Creates a mapper that 
 */
public class JongoScalaMapperFactory implements JongoMapperFactory {

    @Override
    public Mapper create() {
        return new JacksonMapper.Builder().registerModule(new DefaultScalaModule()).build();
    }

}
