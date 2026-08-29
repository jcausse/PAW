package ar.edu.itba.paw.webapp.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan({
    "ar.edu.itba.paw.webapp.controller",
    "ar.edu.itba.paw.service",
    "ar.edu.itba.paw.persistence",
})
@PropertySource("classpath:app.properties")
public class WebConfig implements WebMvcConfigurer {

    /* --------------------------------------------------------------- */
    /* ENVIRONMENT (properties management) */
    /* --------------------------------------------------------------- */

    private final Environment env;

    @Autowired
    public WebConfig(Environment env) {
        this.env = env;
    }

    /* --------------------------------------------------------------- */
    /* FRONTEND (resource management and view resolvers) */
    /* --------------------------------------------------------------- */

    @Bean
    public ViewResolver viewResolver() {
        final var viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    }

    /* --------------------------------------------------------------- */
    /* AUTH (password encoder) */
    /* --------------------------------------------------------------- */

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    /* --------------------------------------------------------------- */
    /* DATABASE (schemas, data sources, initializers and populators) */
    /* --------------------------------------------------------------- */

    @Value("classpath:schema.sql")
    private Resource schema;

    @Bean
    public DataSource dataSource() {
        final var dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl(env.getRequiredProperty("database.jdbc.url"));
        dataSource.setUsername(
            env.getRequiredProperty("database.jdbc.username")
        );
        dataSource.setPassword(
            env.getRequiredProperty("database.jdbc.password")
        );
        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource ds) {
        final var initializer = new DataSourceInitializer();
        initializer.setDataSource(ds);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private ResourceDatabasePopulator databasePopulator() {
        final var populator = new ResourceDatabasePopulator();
        populator.addScript(schema);
        return populator;
    }
}
