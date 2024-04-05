package kmichalski.scoreboard.repostiory;

import kmichalski.scoreboard.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
