package telran.problem.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

import java.util.Set;



@Repository
public interface ProblemRepository extends MongoRepository<Problem,String>{
    Set<Problem> findAllByAuthorIsNotNull();
}
