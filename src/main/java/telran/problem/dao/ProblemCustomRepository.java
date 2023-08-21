package telran.problem.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

@Repository
@AllArgsConstructor
public class ProblemCustomRepository {
    private final MongoTemplate mongoTemplate;
    public void changeAuthorName(String profileId, String newName) {
        Query query = new Query(Criteria.where("authorId").is(profileId));
        Update update = new Update().set("author", newName);
        mongoTemplate.updateMulti(query, update, Problem.class);
    }

    public void deleteProblemsByAuthorId(String authorId) {
        Query query = new Query(Criteria.where("authorId").is(authorId));
        mongoTemplate.remove(query, Problem.class);
    }
}
