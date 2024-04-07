package kmichalski.scoreboard.service;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public Game createNewGame(Team homeTeam, Team awayTeam) {
        Team savedHomeTeam = teamRepository.save(homeTeam);
        Team savedAwayTeam = teamRepository.save(awayTeam);
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
        Game game = gameRepository.findById(gameId).orElseThrow();
        game.setHomeTeamScore(0);
        game.setAwayTeamScore(0);
        game.setGameStatus(GameStatus.IN_PROGRESS);
        return gameRepository.save(game);
    }

    public Game updateGame(Long gameId, Integer newHomeTeamScore, Integer newAwayTeamScore) {
        Game game = gameRepository.findById(gameId).orElseThrow();
        game.setHomeTeamScore(newHomeTeamScore);
        game.setAwayTeamScore(newAwayTeamScore);
        return gameRepository.save(game);
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
}
