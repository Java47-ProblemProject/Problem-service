package telran.problem.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import telran.problem.model.Problem;

import java.util.List;
import java.util.Set;

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

    public List<Problem> findAllByProfileId(String profileId) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("interactions.likes.profileId").is(profileId),
                Criteria.where("interactions.dislikes.profileId").is(profileId),
                Criteria.where("interactions.donations.profileId").is(profileId),
                Criteria.where("interactions.subscriptions.profileId").is(profileId)
        ));
        query.with(Sort.by(Sort.Order.desc("dateCreated")));
        return mongoTemplate.find(query, Problem.class);
    }
}
