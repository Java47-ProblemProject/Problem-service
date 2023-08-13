package telran.problem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf().disable();
        http.authorizeRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,"/problem/createproblem")
                .permitAll()
//                //User section//
                .requestMatchers(HttpMethod.PUT, "/problem/editproblem/{userId}/{problemid}")
                .access("@customSecurity.checkProblemAuthor(problemId, authorId)")

                .anyRequest().permitAll()
                //.authenticated()
        );
        return http.build();
    }
}
