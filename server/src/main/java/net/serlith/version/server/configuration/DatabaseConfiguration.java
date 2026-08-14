package net.serlith.version.server.configuration;

import io.r2dbc.spi.ConnectionFactory;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@NullMarked
@Configuration
public class DatabaseConfiguration {

    @Bean
    public ConnectionFactoryInitializer connectionInitializer(ConnectionFactory factory) {

        final ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(factory);

        final CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("db/migration/V1__init_schema.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

}
