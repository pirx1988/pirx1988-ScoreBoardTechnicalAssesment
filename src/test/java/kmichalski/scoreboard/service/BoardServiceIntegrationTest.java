package kmichalski.scoreboard.service;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // spring context is not loaded between each test method
@ActiveProfiles("test")
public class BoardServiceIntegrationTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardServiceImpl boardService;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();

    }

    @Nested
    class GameRelatedCrudOperationTests {
        private static final int NEW_HOME_TEAM_SCORE = 5;
        private static final int NEW_AWAY_TEAM_SCORE = 4;
        private Team homeTeam;
        private Team awayTeam;

        @BeforeEach
        void setup() {
            homeTeam = Team.builder().name("Poland").build();
            awayTeam = Team.builder().name("Germany").build();
        }

        @Test
        void shouldCreateNewGame() {

            long homeTeamId = teamRepository.save(homeTeam).getId();
            long awayTeamId = teamRepository.save(awayTeam).getId();
            NewGameDto newgameDto = NewGameDto.builder()
                    .homeTeamId(homeTeamId)
                    .awayTeamId(awayTeamId)
                    .build();

            // Act
            boardService.createNewGame(newgameDto);

            List<Game> savedGames = gameRepository.findAll();
            assertEquals(1, savedGames.size());

            Game savedGame = savedGames.get(0);
            assertEquals(GameStatus.NEW, savedGame.getGameStatus());
            assertEquals(homeTeam.getName(), savedGame.getHomeTeam().getName());
            assertEquals(awayTeam.getName(), savedGame.getAwayTeam().getName());
            assertNull(savedGame.getHomeTeamScore());
            assertNull(savedGame.getAwayTeamScore());
        }


        @Test
        void shouldCorrectlySetInitialGameState_whenNewGameStarted() {
            teamRepository.save(homeTeam);
            teamRepository.save(awayTeam);

            Game newGame = Game.builder().homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .gameStatus(GameStatus.NEW)
                    .build();
            gameRepository.save(newGame);

            // Act
            boardService.startNewGame(newGame.getId());

            Game startedGame = gameRepository.findById(newGame.getId()).orElseThrow();

            assertEquals(0, startedGame.getHomeTeamScore());
            assertEquals(0, startedGame.getAwayTeamScore());
            assertEquals(GameStatus.IN_PROGRESS, startedGame.getGameStatus());
        }

        @Test
        void shouldCorrectlyUpdateGame() {
            teamRepository.save(homeTeam);
            teamRepository.save(awayTeam);

            Game gameInProgress = Game.builder().homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .homeTeamScore(NEW_HOME_TEAM_SCORE - 10)
                    .awayTeamScore(NEW_AWAY_TEAM_SCORE - 10)
                    .gameStatus(GameStatus.IN_PROGRESS)
                    .build();
            gameRepository.save(gameInProgress);

            UpdateGameDto updateGameDto = UpdateGameDto.builder()
                    .id(gameInProgress.getId())
                    .homeTeamScore(NEW_HOME_TEAM_SCORE)
                    .awayTeamScore(NEW_AWAY_TEAM_SCORE)
                    .version(gameInProgress.getVersion())
                    .build();

            // Act
            boardService.updateGame(updateGameDto);

            Game updatedGame = gameRepository.findById(gameInProgress.getId()).orElseThrow();

            assertEquals(GameStatus.IN_PROGRESS, updatedGame.getGameStatus());
            assertEquals(NEW_HOME_TEAM_SCORE, updatedGame.getHomeTeamScore());
            assertEquals(NEW_AWAY_TEAM_SCORE, updatedGame.getAwayTeamScore());
        }

        @Test
        void shouldThrowOptimisticLockingFailureException_whenGameInconsistencyOccured() {
            teamRepository.save(homeTeam);
            teamRepository.save(awayTeam);

            Game gameInProgress = Game.builder().homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .homeTeamScore(NEW_HOME_TEAM_SCORE - 10)
                    .awayTeamScore(NEW_AWAY_TEAM_SCORE - 10)
                    .gameStatus(GameStatus.IN_PROGRESS)
                    .build();
            gameRepository.save(gameInProgress);

            UpdateGameDto updateGameDto = UpdateGameDto.builder()
                    .id(gameInProgress.getId())
                    .homeTeamScore(NEW_HOME_TEAM_SCORE)
                    .awayTeamScore(NEW_AWAY_TEAM_SCORE)
                    .version(gameInProgress.getVersion())
                    .build();

            gameRepository.save(gameInProgress);

            // Act
            assertThrows(OptimisticLockingFailureException.class, () -> boardService.updateGame(updateGameDto));
        }

        @Test
        void shouldCorrectlyFinishGame() {
            teamRepository.save(homeTeam);
            teamRepository.save(awayTeam);

            Game gameInProgress = Game.builder().homeTeam(homeTeam)
                    .awayTeam(awayTeam)
                    .homeTeamScore(NEW_HOME_TEAM_SCORE)
                    .awayTeamScore(NEW_AWAY_TEAM_SCORE)
                    .gameStatus(GameStatus.IN_PROGRESS)
                    .build();
            gameRepository.save(gameInProgress);

            // Act
            boardService.finishGame(gameInProgress.getId());

            Game finishedGame = gameRepository.findById(gameInProgress.getId()).orElseThrow();

            assertEquals(GameStatus.FINISHED, finishedGame.getGameStatus());
        }

    }


    @Nested
    public class GamesByTotalScoreTests {
        @Test
        void shouldReturnInProgressGameWithTheSameTotalScore() {
            createGameWithScore("t1", "t2", 1, 2, GameStatus.IN_PROGRESS);
            createGameWithScore("t5", "t6", 1, 3, GameStatus.FINISHED);
            Game gamInProgressWithExpectedTotalScore1 = createGameWithScore("t3", "t4", 2, 2, GameStatus.IN_PROGRESS);
            Game gamInProgressWithExpectedTotalScore2 = createGameWithScore("t5", "t6", 3, 1, GameStatus.IN_PROGRESS);
            List<Game> expectedInProgressGamesByTotalScore = List.of(gamInProgressWithExpectedTotalScore2, gamInProgressWithExpectedTotalScore1);

            // Act
            final int TOTAL_SCORE = 4;
            List<Game> actualInProgressGamesByTotalScore = gameRepository.findInProgressGamesByTotalScore(TOTAL_SCORE);

            assertEquals(2, actualInProgressGamesByTotalScore.size());
            // verify if games are correctly fetched by TOTAL_SCORE and correctly ordered in descending mode by the most recently added to the system
            assertThat(actualInProgressGamesByTotalScore)
                    .containsExactlyElementsOf(expectedInProgressGamesByTotalScore);
        }

        private Game createGameWithScore(String homeTeamName, String awayTeamName, int homeTeamScore, int awayTeamScore, GameStatus status) {
            Team homeTeam = teamRepository.save(Team.builder().name(homeTeamName).build());
            Team awayTeam = teamRepository.save(Team.builder().name(awayTeamName).build());
            Game game = Game.builder()
                    .gameStatus(status)
                    .homeTeam(homeTeam)
                    .homeTeamScore(homeTeamScore)
                    .awayTeam(awayTeam)
                    .awayTeamScore(awayTeamScore)
                    .build();
            return gameRepository.save(game);
        }
    }
}