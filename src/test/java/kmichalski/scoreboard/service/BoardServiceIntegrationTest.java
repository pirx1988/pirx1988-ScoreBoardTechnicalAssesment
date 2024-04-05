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

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private TeamRepository teamRepository;


    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void shouldCreateNewGame() {
        Team homeTeam = Team.builder().name("Poland").build();
        Team awayTeam = Team.builder().name("Germany").build();

        //Act
        Game newGame = boardService.createNewGame(homeTeam, awayTeam);

        List<Game> savedGames = gameRepository.findAll();
        assertEquals(1, savedGames.size());

        Game savedGame = savedGames.get(0);
        assertEquals(GameStatus.NEW, savedGame.getGameStatus());
        assertEquals(homeTeam.getName(), savedGame.getHomeTeam().getName());
        assertEquals(awayTeam.getName(), savedGame.getAwayTeam().getName());
        assertNull(savedGame.getHomeTeamScore());
        assertNull(savedGame.getAwayTeamScore());
    }
}
