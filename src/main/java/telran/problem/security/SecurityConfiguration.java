package telran.problem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.PUT, "/problem/editproblem/{userId}/{problemId}")
                        .access("@customSecurity.checkProblemAuthor(#problemId)")

                        .requestMatchers(HttpMethod.DELETE, "/problem/deleteproblem/{userId}/{problemId}")
                        .access("@customSecurity.checkProblemAuthor(#problemId)")

                        .requestMatchers(HttpMethod.DELETE, "problem/deleteproblem/{problemId}")
                        .access("hasRole('ROLE_ADMINISTRATOR')")

                        .anyRequest()
                        .authenticated()
        );
        return http.build();
    }
}
