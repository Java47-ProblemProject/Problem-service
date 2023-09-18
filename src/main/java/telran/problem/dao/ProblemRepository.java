package telran.problem.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

import java.util.Set;
import java.util.stream.Stream;


@Repository
public interface ProblemRepository extends MongoRepository<Problem, String> {
    Stream<Problem> findAllByCommunityNamesContaining(Set<String> communities);
    @Query("{$or: [{'interactions.likes.profileId': ?0}, {'interactions.dislikes.profileId': ?0}, {'interactions.donations.profileId': ?0}, {'interactions.subscriptions.profileId': ?0}]}")
    Stream<Problem> findAllByProfileId(String profileId);
}
