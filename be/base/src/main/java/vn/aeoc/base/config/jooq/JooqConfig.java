package vn.aeoc.base.config.jooq;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.logging.LogManager;

@Configuration
public class JooqConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DSLContext dslContext() {
        LogManager.getLogManager().reset();
        return DSL.using(dataSource, SQLDialect.MYSQL);
    }
}
