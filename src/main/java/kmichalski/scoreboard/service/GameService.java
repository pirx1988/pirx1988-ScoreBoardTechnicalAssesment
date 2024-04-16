package kmichalski.scoreboard.service;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.model.Game;

import java.util.List;

public interface GameService {
    Game createNewGame(NewGameDto newgameDto);

    Game startNewGame(Long gameId);

    Game updateGame(UpdateGameDto updatedGameDto);

    Long finishGame(Long gameId);

    List<Game> getGamesByTotalScore(int totalScore);

    List<Game> getAllUnfinishedGames();

    Game getInProgressGame(long gameId);
}
