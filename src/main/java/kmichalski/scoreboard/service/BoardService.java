package kmichalski.scoreboard.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.exception.NegativeTeamScoreException;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.OptimisticLock;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    // region Create new game
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
    // endregion

    // region Start New game
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
    // endregion

    // region Update game
    @Transactional
    @Lock(LockModeType.OPTIMISTIC)
    public Game updateGame(UpdateGameDto updatedGameDto) {
        validateGameScores(updatedGameDto);
        try {
            // Retrieve the existing game entity from the database
            Game game = gameRepository.findById(updatedGameDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + updatedGameDto.getId()));

            // Apply changes to the existing game entity
            Game updatedGame = new Game();
            updatedGame.setVersion(updatedGameDto.getVersion());
            updatedGame.setHomeTeam(game.getHomeTeam());
            updatedGame.setAwayTeam(game.getAwayTeam());
            updatedGame.setHomeTeamScore(updatedGameDto.getHomeTeamScore());
            updatedGame.setAwayTeamScore(updatedGameDto.getAwayTeamScore());
            updatedGame.setId(updatedGameDto.getId());
            updatedGame.setGameStatus(game.getGameStatus());
            gameRepository.save(updatedGame);
            return updatedGame;

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Failed to update game due to Optimistic conflict. ", ex);
        }
    }
    // endregion

    // region Finish game
    public Long finishGame(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Game not found with Id: " + gameId));
        if (game.getGameStatus() != GameStatus.IN_PROGRESS) {
            throw new ImproperStatusGameException("It's forbidden to finish game with Id: " + gameId +
                    "which has other status than IN_PROGRESS. Actual status: " + game.getGameStatus());
        }
        game.setGameStatus(GameStatus.FINISHED);
        return gameRepository.save(game).getId();
    }
    // endregion

    // region Games fetch
    public List<Game> getGamesByTotalScore(int totalScore) {
        return gameRepository.findInProgressGamesByTotalScore(totalScore);
    }

    public List<Game> getAllUnfinishedGames() {
        return gameRepository.findByGameStatusNot(GameStatus.FINISHED);
    }

    public Game getInProgressGame(long gameId) {
        return gameRepository.findByIdAndGameStatus(gameId, GameStatus.IN_PROGRESS).orElseThrow(
                () -> new NoSuchElementException("Game not found with Id: " + gameId)
        );
    }
    //endregion

    //region Validation helpers
    private static void validateGameScores(UpdateGameDto updatedGameDto) {
        if (updatedGameDto.getHomeTeamScore() < 0) {
            throw new NegativeTeamScoreException("Negative Home Team score for gameId: " + updatedGameDto.getId());
        }
        if (updatedGameDto.getAwayTeamScore() < 0) {
            throw new NegativeTeamScoreException("Negative Away Team score for gameId: " + updatedGameDto.getId());
        }
    }
    // endregion
}
