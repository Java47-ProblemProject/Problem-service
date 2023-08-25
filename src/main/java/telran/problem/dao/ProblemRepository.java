package telran.problem.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


@Repository
public interface ProblemRepository extends MongoRepository<Problem, String> {
    Stream<Problem> findAllByCommunityNamesContaining(Set<String> communities);

    Stream<Problem> findAllByAuthorId(String profileId);
}
