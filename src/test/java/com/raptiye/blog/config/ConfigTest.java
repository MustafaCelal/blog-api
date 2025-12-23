package com.raptiye.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConfigTest {

    @Test
    void openApiConfigShouldCreateBean() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.openAPI();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Blog API");
    }

    @Test
    void securityConfigBeans() {
        SecurityConfig config = new SecurityConfig(null, null);
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();

        AuthenticationProvider provider = config.authenticationProvider();
        assertThat(provider).isNotNull();
    }

    @Test
    void jpaConfigExists() {
        JpaConfig jpaConfig = new JpaConfig();
        assertThat(jpaConfig).isNotNull();
    }
}
