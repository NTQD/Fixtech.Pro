package vn.aeoc.rbac.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "internal.app")
public class SystemProperties {
    private String appApiKey;
}
