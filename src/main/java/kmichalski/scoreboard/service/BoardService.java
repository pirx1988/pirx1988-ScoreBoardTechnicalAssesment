package kmichalski.scoreboard.service;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.exception.NegativeTeamScoreException;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public Game createNewGame(NewGameDto newgameDto) {
        Team savedHomeTeam = teamRepository.findById(newgameDto.getHomeTeamId()).orElseThrow(
                () -> new NoSuchElementException("Team not found with Id: " + newgameDto.getHomeTeamId())
        );
        Team savedAwayTeam = teamRepository.findById(newgameDto.getAwayTeamId()).orElseThrow(
                () -> new NoSuchElementException("Team not found with Id: " + newgameDto.getAwayTeamId())
        );
        Game newGame = Game.builder()
                .gameStatus(GameStatus.NEW)
                .homeTeam(savedHomeTeam)
                .awayTeam(savedAwayTeam)
                .awayTeamScore(null)
                .homeTeamScore(null)
                .build();
        return gameRepository.save(newGame);
    }

    public Game startNewGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Game not found with Id: " + gameId)
        );
        if (!game.getGameStatus().equals(GameStatus.NEW)) {
            throw new ImproperStatusGameException("Improper Status game: " + game.getGameStatus()
                    + "Game with id: " + gameId + " not started." + "Expected Game status: " + GameStatus.NEW);
        }

        game.setHomeTeamScore(0);
        game.setAwayTeamScore(0);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        return gameRepository.save(game);
    }

    public Game updateGame(Long gameId, Integer newHomeTeamScore, Integer newAwayTeamScore) {
        validateGameScores(gameId, newHomeTeamScore, newAwayTeamScore);
        Game game = gameRepository.findById(gameId).orElseThrow();
        game.setHomeTeamScore(newHomeTeamScore);
        game.setAwayTeamScore(newAwayTeamScore);
        return gameRepository.save(game);
    }

    private static void validateGameScores(Long gameId, Integer newHomeTeamScore, Integer newAwayTeamScore) {
        if(newHomeTeamScore < 0) {
            throw new NegativeTeamScoreException("Negative Home Team score for gameId: " + gameId);
        }
        if (newAwayTeamScore < 0) {
            throw new NegativeTeamScoreException("Negative Away Team score for gameId: " + gameId);
        }
    }

    // TODO: Remember about edge case when. Game can be finished only when in progress
    public Long finishGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        game.setGameStatus(GameStatus.FINISHED);
        return gameRepository.save(game).getId();
    }

    public List<Game> getAllUnfinishedGames() {
        return gameRepository.findByGameStatusNot(GameStatus.FINISHED);
    }

    public Game getInProgressGame(long gameId) { // TODO: Consider check status as well or change method name to just getGame
        return gameRepository.findByIdAndGameStatus(gameId, GameStatus.IN_PROGRESS).orElseThrow(
                () -> new NoSuchElementException("Game not found with Id: " + gameId)
        );
    }
}
