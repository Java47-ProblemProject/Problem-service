package telran.problem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
//                       //User section//
                        .requestMatchers(HttpMethod.PUT, "/problem/editproblem/{userId}/{problemid}")
                        .access(new WebExpressionAuthorizationManager("@customSecurity.checkProblemAuthor(#problemid, #userId)"))

                        .anyRequest()
                        .authenticated()
        );
        return http.build();
    }
}
