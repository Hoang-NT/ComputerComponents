package fa.training.components.configuration;

import fa.training.components.security.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
    // This configuration file has @EnableJpaAuditing to use @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy

    // Create Bean of AuditorAware to get username for @CreatedBy, @LastModifiedBy
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
