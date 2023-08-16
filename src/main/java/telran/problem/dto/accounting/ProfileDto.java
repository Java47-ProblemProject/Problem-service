package telran.problem.dto.accounting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@AllArgsConstructor //must be deleted after test
@NoArgsConstructor //must be deleted after test
@ToString

@Getter
public class ProfileDto {
    protected String username;
    protected String email;
    protected String educationLevel;
    protected Set<String> communities;
    protected LocationDto location;
    protected String password;
    protected Set<String> roles;
    protected String avatar;
    protected StatsDto stats;
    protected Set<ActivityDto> activities;
    protected Double wallet;
}
