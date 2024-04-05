package kmichalski.scoreboard.repostiory;

import kmichalski.scoreboard.model.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Make sure everything is fetched in one sql query instead of N+1.
    // EntityGraph.EntityGraphType.FETCH means that only the specified attributes are retrieved
    @EntityGraph(value = "game-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<Game> findAll();
}
