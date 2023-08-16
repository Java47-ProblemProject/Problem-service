package telran.problem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Reactions {
    protected Integer likes = 0;
    protected Integer dislikes = 0;

    public void addLike() {
        this.likes++;
    }

    public void addDislike() {
        this.dislikes++;
    }

    public void removeLike() {
        this.likes--;
    }

    public void removeDislike() {
        this.dislikes--;
    }
}

