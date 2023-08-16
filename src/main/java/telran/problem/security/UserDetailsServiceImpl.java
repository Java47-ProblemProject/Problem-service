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
import telran.problem.dto.accounting.ProfileDto;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {
    final KafkaConsumer kafkaConsumer;

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String encryptedEmail = EmailEncryptionConfiguration.encryptAndEncodeUserId(email);
        //kafkaConsumer.addUser1(null);
        ProfileDto profile = kafkaConsumer.getProfile();
        System.out.println(profile);
        if (profile != null && encryptedEmail.equals(profile.getEmail())) {
            return new User(profile.getEmail(), profile.getPassword(), AuthorityUtils.createAuthorityList(profile.getRoles()));
        } else throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Wrong email");
    }
}
