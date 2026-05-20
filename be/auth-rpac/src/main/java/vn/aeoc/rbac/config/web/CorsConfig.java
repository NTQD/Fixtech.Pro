package vn.aeoc.rbac.config.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*").exposedHeaders("*").allowCredentials(true).maxAge(3600);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Allow specific methods
        List<String> allowedMethods = new ArrayList<>();
        allowedMethods.add(HttpMethod.POST.name());
        allowedMethods.add(HttpMethod.GET.name());
        allowedMethods.add(HttpMethod.PUT.name());
        allowedMethods.add(HttpMethod.DELETE.name());
        allowedMethods.add(HttpMethod.OPTIONS.name());
        configuration.setAllowedMethods(allowedMethods);

        // Allow any headers and expose them
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Max age
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
