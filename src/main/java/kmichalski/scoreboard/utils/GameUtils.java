package kmichalski.scoreboard.utils;

import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.exception.NegativeTeamScoreException;

public class GameUtils {

    //region Validation helpers
    public static void validateGameScores(UpdateGameDto updatedGameDto) {
        if (updatedGameDto.getHomeTeamScore() < 0) {
            throw new NegativeTeamScoreException("Negative Home Team score for gameId: " + updatedGameDto.getId());
        }
        if (updatedGameDto.getAwayTeamScore() < 0) {
            throw new NegativeTeamScoreException("Negative Away Team score for gameId: " + updatedGameDto.getId());
        }
    }
    // endregion
}
