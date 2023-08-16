package telran.problem.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.dto.accounting.ActivityDto;
import telran.problem.dto.accounting.LocationDto;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.dto.accounting.StatsDto;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {
    final KafkaConsumer kafkaConsumer;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String encryptedEmail = EmailEncryptionConfiguration.encryptAndEncodeUserId(email);
        addUser2();
        ProfileDto profile = kafkaConsumer.getProfile();
        if (profile != null && encryptedEmail.equals(profile.getEmail())) {
            return new User(profile.getEmail(), profile.getPassword(), AuthorityUtils.createAuthorityList(profile.getRoles()));
        } else throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "User not found");
    }


    private void addUser1() {
        Set<String> communities = new HashSet<>(Set.of("Algebra", "Probability", "Calculus"));
        LocationDto location = new LocationDto("Israel", "Tel-Aviv");
        StatsDto stats = new StatsDto(0, 0, 0, 10);
        Set<String> roles = new HashSet<>(Set.of("USER"));
        ProfileDto profile = new ProfileDto("JAN_Application@protonmail.com", "JsOTQMORw7Z4OMO-wofDusOVZcO5w4XDpV7Dt8OOw49_DXIAwqUMw5tNIFLCslIN",
                "OTHER", communities, location, "$2a$10$RTjdb8fn2jyExBE.Yn0kXumxA77yspyGfM/1VhvYsswAZttOQFuuC",
                roles, "", stats, new HashSet<ActivityDto>(), 0.0);
        kafkaConsumer.setProfile(profile);
        //JAN_Application@protonmail.com, password
    }

    private void addUser2() {
        Set<String> communities = new HashSet<>(Set.of("Algebra", "Probability", "Calculus"));
        LocationDto location = new LocationDto("Israel", "Tel-Aviv");
        StatsDto stats = new StatsDto(0, 0, 0, 10);
        Set<String> roles = new HashSet<>(Set.of("ADMINISTRATOR"));
        ProfileDto profile = new ProfileDto("1JAN_Application@protonmail.com", "w6wbCn3DtMOuegQ0wq3CgMKNXcOlQ3XCrXPCgzDDvhcmwrfDijvDhEdmXSLDqA",
                "OTHER", communities, location, "$2a$10$/tHwKw6Sg5NY.KYmUHvsGerwj9YGqraEhFyFgE8b1EvlbObJXEVu.",
                roles, "", stats, new HashSet<ActivityDto>(), 0.0);
        kafkaConsumer.setProfile(profile);
        //1JAN_Application@protonmail.com, password
    }
}