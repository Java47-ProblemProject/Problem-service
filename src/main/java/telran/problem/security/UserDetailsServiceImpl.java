package telran.problem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import telran.problem.configuration.KafkaConsumer;
import telran.problem.dto.accounting.ProfileDto;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService {
    final KafkaConsumer kafkaConsumer;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ProfileDto profile = kafkaConsumer.getProfile();
        if(profile != null) {
         return new User(profile.getEmail(), profile.getPassword(), AuthorityUtils.createAuthorityList(profile.getRoles()));
        }
        return new User("Unknown","Unknown",AuthorityUtils.createAuthorityList());
    }



}
