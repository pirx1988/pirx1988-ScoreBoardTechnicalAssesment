package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.dto.GameDto;
import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.service.BoardService;
import kmichalski.scoreboard.service.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = {BoardController.class})
@ContextConfiguration(classes = {BoardController.class, GlobalExceptionController.class})
class BoardControllerTest {
    private static final Long HOME_TEAM_ID = 1L;
    private static final Long AWAY_TEAM_ID = 2L;
    private static final long GAME_ID = 3L;

    @MockBean
    private BoardService boardService;

    @MockBean
    private TeamService teamService;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        reset(boardService);
    }

    @Test
    void showBoardWithGames() throws Exception {
        Team homeTeam = Team.builder().name("Poland").build();
        Team awayTeam = Team.builder().name("England").build();
        Game game = Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(0)
                .awayTeamScore(0)
                .gameStatus(GameStatus.IN_PROGRESS).build();
        when(boardService.getAllUnfinishedGames()).thenReturn(List.of(game));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("unfinishedGames"))
                .andExpect(view().name("board.html"));
    }

    @Test
    void showUpdatedBoard_whenGameStartedWithProperNewStatus() throws Exception {
        mockMvc.perform(post("/start-game/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    void showErrorPage_whenAttemptToGameStartWithImproperStatus() throws Exception {
        doThrow(new ImproperStatusGameException("Improper Status game: IN_PROGRESS Game with id: 123 not started. Expected Game status: NEW"))
                .when(boardService).startNewGame(GAME_ID);

        mockMvc.perform(post("/start-game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Improper Status game: IN_PROGRESS Game with id: 123 not started. Expected Game status: NEW"));
    }

    @Test
    void showErrorPage_whenAttemptToStartGameWhichNotExists() throws Exception {
        doThrow(new NoSuchElementException("Game not found with Id: " + GAME_ID))
                .when(boardService).startNewGame(GAME_ID);

        mockMvc.perform(post("/start-game/"+ GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Game not found with Id: " + GAME_ID));
    }

    @Test
    void redirectToBoardView_whenNewGameCorrectlyCreated() throws Exception {
        mockMvc.perform(post("/create-new-game")
                        .param("homeTeamId", HOME_TEAM_ID.toString())
                        .param("awayTeamId", AWAY_TEAM_ID.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(boardService, times(1)).createNewGame(any(GameDto.class));
    }

    @Test
    void shouldShowAddNewGameViewAgainWithErrors_whenNeAttemptToCreateGameWithTheSameTeams() throws Exception {
        mockMvc.perform(post("/create-new-game")
                        .param("homeTeamId", HOME_TEAM_ID.toString())
                        .param("awayTeamId", HOME_TEAM_ID.toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("create_new_game.html"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));

        verify(boardService, never()).createNewGame(any(GameDto.class));
    }

    @Test
    void shouldShowErrorPage_whenAttemptTCreateGameWhilstHomeTeamOrAwayNotExists() throws Exception {
        doThrow(new NoSuchElementException("Team not found with Id: " + HOME_TEAM_ID))
                .when(boardService).createNewGame(any(GameDto.class));

        mockMvc.perform(post("/create-new-game").
                        param("homeTeamId", HOME_TEAM_ID.toString())
                        .param("awayTeamId", AWAY_TEAM_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Team not found with Id: " + HOME_TEAM_ID));
    }
}