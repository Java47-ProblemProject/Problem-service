package telran.problem.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

import java.util.Set;
import java.util.stream.Stream;


@Repository
public interface ProblemRepository extends MongoRepository<Problem, String> {
    Stream<Problem> findAllByCommunityNamesContainingOrderByDateCreatedDesc(Set<String> communities);
    Stream<Problem> findAllByOrderByDateCreatedDesc();
}
