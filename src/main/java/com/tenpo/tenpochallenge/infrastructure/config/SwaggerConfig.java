package com.tenpo.tenpochallenge.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for API documentation
 * 
 * @author Wilmar Garcia (wilmar.garciava@gmail.com)
 * @see <a href="https://github.com/WilDevp">GitHub Profile</a>
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI tenpoAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url(contextPath).description("Default Server URL"))
            .info(new Info()
                .title("Tenpo Challenge API")
                .description("REST API for performing calculations with dynamic percentage - Redis Cache Implementation")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Wilmar Garcia")
                    .email("wilmar.garciava@gmail.com")
                    .url("https://github.com/WilDevp"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}