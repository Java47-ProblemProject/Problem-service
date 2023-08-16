package telran.problem.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import telran.problem.dto.accounting.ActivityDto;
import telran.problem.dto.accounting.LocationDto;
import telran.problem.dto.accounting.ProfileDto;
import telran.problem.dto.accounting.StatsDto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Getter
@Configuration
public class KafkaConsumer {
    @Setter
    ProfileDto profile;

    @Bean
    protected Consumer<ProfileDto> receiveData() {
        return data -> {
            this.profile = data;
        };
    }

    //FOR TEST ONLY ------->
//    public void addUser1(ProfileDto updatedProfile) {
//        Set<String> communities = new HashSet<>(Set.of("Algebra", "Probability", "Calculus"));
//        LocationDto location = new LocationDto("Israel", "Tel-Aviv");
//        StatsDto stats = new StatsDto(0, 0, 0, 10);
//        Set<String> roles = new HashSet<>(Set.of("USER"));
//        ProfileDto newProfile = new ProfileDto("JAN_Application@protonmail.com", "JsOTQMORw7Z4OMO-wofDusOVZcO5w4XDpV7Dt8OOw49_DXIAwqUMw5tNIFLCslIN",
//                "OTHER", communities, location, "$2a$10$RTjdb8fn2jyExBE.Yn0kXumxA77yspyGfM/1VhvYsswAZttOQFuuC",
//                roles, "", stats, new HashMap<String, ActivityDto>(), 0.0);
//        if (updatedProfile == null) {
//            this.profile = newProfile;
//        } else this.profile = updatedProfile;
//        //JAN_Application@protonmail.com, password
//    }
//
//    public void addUser2(ProfileDto updatedProfile) {
//        Set<String> communities = new HashSet<>(Set.of("Algebra", "Probability", "Calculus"));
//        LocationDto location = new LocationDto("Israel", "Tel-Aviv");
//        StatsDto stats = new StatsDto(0, 0, 0, 10);
//        Set<String> roles = new HashSet<>(Set.of("USER", "ADMINISTRATOR"));
//        ProfileDto newProfile = new ProfileDto("1JAN_Application@protonmail.com", "w6wbCn3DtMOuegQ0wq3CgMKNXcOlQ3XCrXPCgzDDvhcmwrfDijvDhEdmXSLDqA",
//                "OTHER", communities, location, "$2a$10$/tHwKw6Sg5NY.KYmUHvsGerwj9YGqraEhFyFgE8b1EvlbObJXEVu.",
//                roles, "", stats, new HashMap<String, ActivityDto>(), 0.0);
//        if (updatedProfile == null) {
//            this.profile = newProfile;
//        } else this.profile = updatedProfile;
//        //1JAN_Application@protonmail.com, password
//    }
}
