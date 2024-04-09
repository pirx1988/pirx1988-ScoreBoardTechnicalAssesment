package kmichalski.scoreboard.repostiory;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Make sure everything is fetched in one sql query instead of N+1.
    // EntityGraph.EntityGraphType.FETCH means that only the specified attributes are retrieved
    @EntityGraph(value = "game-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<Game> findAll();

    @EntityGraph(value = "game-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<Game> findByGameStatusNot(GameStatus gameStatus);

    Optional<Game> findByIdAndGameStatus(Long gameId, GameStatus gameStatus);

    @EntityGraph(value = "game-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT g FROM Game g WHERE g.gameStatus = kmichalski.scoreboard.model.GameStatus.IN_PROGRESS AND (g.homeTeamScore + g.awayTeamScore = :totalScore)")
    List<Game> findInProgressGamesByTotalScore(int totalScore);
}
