package telran.problem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Reactions {
    protected Integer likes = 0;
    @Setter
    protected Double likesWeight = 0.;
    protected Integer dislikes = 0;
    @Setter
    protected Double dislikesWeight = 0.;

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

