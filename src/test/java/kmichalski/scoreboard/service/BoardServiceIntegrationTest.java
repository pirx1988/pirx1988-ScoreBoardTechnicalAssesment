package kmichalski.scoreboard.service;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // spring context is not loaded between each test method
@ActiveProfiles("test")
public class BoardServiceIntegrationTest {

    private static final int NEW_HOME_TEAM_SCORE = 5;
    private static final int NEW_AWAY_TEAM_SCORE = 4;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private TeamRepository teamRepository;
    private Team homeTeam;
    private Team awayTeam;


    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        homeTeam = Team.builder().name("Poland").build();
        awayTeam = Team.builder().name("Germany").build();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void shouldCreateNewGame() {
        // Act
        boardService.createNewGame(homeTeam, awayTeam);

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
    void shouldThrowImproperGameStatusException_whenAttemptToStartGameWithImproperStatus() {
        // TODO: Write integration test
    }

    @Test
    void shouldThrowImproperGameStatusException_whenAttemptToStartGameWhichNotExists(){
        // TODO: Write integration test
    }

    @Test
    void shouldCorrectlyUpdateGame() {
        teamRepository.save(homeTeam);
        teamRepository.save(awayTeam);

        Game gameInProgress = Game.builder().homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(NEW_HOME_TEAM_SCORE-10)
                .awayTeamScore(NEW_AWAY_TEAM_SCORE-10)
                .gameStatus(GameStatus.IN_PROGRESS)
                .build();
        gameRepository.save(gameInProgress);

        // Act
        boardService.updateGame(gameInProgress.getId(), NEW_HOME_TEAM_SCORE, NEW_AWAY_TEAM_SCORE);

        Game updatedGame = gameRepository.findById(gameInProgress.getId()).orElseThrow();

        assertEquals(GameStatus.IN_PROGRESS, updatedGame.getGameStatus());
        assertEquals(NEW_HOME_TEAM_SCORE, updatedGame.getHomeTeamScore());
        assertEquals(NEW_AWAY_TEAM_SCORE, updatedGame.getAwayTeamScore());
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
