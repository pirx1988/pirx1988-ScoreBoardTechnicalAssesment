package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.mapper.GameDtoMapper;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.service.BoardServiceImpl;
import kmichalski.scoreboard.service.TeamServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
@ContextConfiguration(classes = {BoardController.class, GlobalExceptionController.class, GameDtoMapper.class, ModelMapper.class})
class BoardControllerTest {
    private static final Long HOME_TEAM_ID = 1L;
    private static final Long AWAY_TEAM_ID = 2L;
    private static final long GAME_ID = 3L;
    private static final Integer TOTAL_SCORE = 10;
    private static final Integer NEGATIVE_TOTAL_SCORE = -10;

    @MockBean
    private BoardServiceImpl boardService;
    @MockBean
    private TeamServiceImpl teamServiceImpl;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        reset(boardService);
    }

    // region Display board
    @Test
    void showBoardWithAllUnfinishedGames_whenNoTotalScoreFilter() throws Exception {
        Game game = createGame();
        when(boardService.getAllUnfinishedGames()).thenReturn(List.of(game));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("unfinishedGames"))
                .andExpect(view().name("board.html"));
        verify(boardService, times(1)).getAllUnfinishedGames();
        verify(boardService, never()).getGamesByTotalScore(anyInt());
    }

    @Test
    void showBoardWithFilteredUnfinishedGamesByTotalScore_whenCorrectTotalScoreFilterValuePassed() throws Exception {
        Game game = createGame();
        when(boardService.getGamesByTotalScore(TOTAL_SCORE)).thenReturn(List.of(game));

        mockMvc.perform(get("/?total_score=" + TOTAL_SCORE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("unfinishedGames"))
                .andExpect(view().name("board.html"));
        verify(boardService, times(1)).getGamesByTotalScore(TOTAL_SCORE);
        verify(boardService, never()).getAllUnfinishedGames();
    }

    @Test
    void showErrorPage_whenWhenAttemptToPassNegativeTotalScoreInQueryStringToBoardView() throws Exception {
        mockMvc.perform(get("/?total_score=" + NEGATIVE_TOTAL_SCORE))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Total score cannot be negative number. Passed value: " + NEGATIVE_TOTAL_SCORE));
        verify(boardService, never()).getGamesByTotalScore(NEGATIVE_TOTAL_SCORE);
        verify(boardService, never()).getAllUnfinishedGames();
    }

    @Test
    void showErrorPage_whenWhenAttemptToPassTotalScoreUnableToParseAsIntegerFormatInQueryStringToBoardView() throws Exception {
        String TOTAL_SCORE_IN_INCORRECT_FORMAT = "abc10";
        mockMvc.perform(get("/?total_score=" + TOTAL_SCORE_IN_INCORRECT_FORMAT))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Total score must be in a valid format number. Passed value: " + TOTAL_SCORE_IN_INCORRECT_FORMAT));
        verify(boardService, never()).getGamesByTotalScore(anyInt());
        verify(boardService, never()).getAllUnfinishedGames();
    }
    //endregion

    // region Create new game
    @Test
    void redirectToBoardView_whenNewGameCorrectlyCreated() throws Exception {
        mockMvc.perform(post("/create-new-game")
                        .param("homeTeamId", HOME_TEAM_ID.toString())
                        .param("awayTeamId", AWAY_TEAM_ID.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(boardService, times(1)).createNewGame(any(NewGameDto.class));
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

        verify(boardService, never()).createNewGame(any(NewGameDto.class));
    }

    @Test
    void shouldShowErrorPage_whenAttemptTCreateGameWhilstHomeTeamOrAwayNotExists() throws Exception {
        doThrow(new NoSuchElementException("Team not found with Id: " + HOME_TEAM_ID))
                .when(boardService).createNewGame(any(NewGameDto.class));

        mockMvc.perform(post("/create-new-game").
                        param("homeTeamId", HOME_TEAM_ID.toString())
                        .param("awayTeamId", AWAY_TEAM_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Team not found with Id: " + HOME_TEAM_ID));

        verify(boardService, times(1)).createNewGame(any(NewGameDto.class));

    }
    // endregion

    // region Start game
    @Test
    void showUpdatedBoard_whenGameStartedToProperNewStatus() throws Exception {
        mockMvc.perform(post("/start-game/" + GAME_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(boardService, times(1)).startNewGame(GAME_ID);
    }

    @Test
    void showErrorPage_whenAttemptToStartGameWithImproperStatus() throws Exception {
        doThrow(new ImproperStatusGameException("Improper Status game: IN_PROGRESS Game with id: 123 not started. Expected Game status: NEW"))
                .when(boardService).startNewGame(GAME_ID);

        mockMvc.perform(post("/start-game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Improper Status game: IN_PROGRESS Game with id: 123 not started. Expected Game status: NEW"));

        verify(boardService, times(1)).startNewGame(GAME_ID);
    }

    @Test
    void showErrorPage_whenAttemptToStartGameWhichNotExists() throws Exception {
        doThrow(new NoSuchElementException("Game not found with Id: " + GAME_ID))
                .when(boardService).startNewGame(GAME_ID);

        mockMvc.perform(post("/start-game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Game not found with Id: " + GAME_ID));

        verify(boardService, times(1)).startNewGame(GAME_ID);
    }
    // endregion

    // region Update game
    @Test
    void showUpdateGameForm() throws Exception {
        Team homeTeam = Team.builder().name("Poland").id(HOME_TEAM_ID).build();
        Team awayTeam = Team.builder().name("England").id(AWAY_TEAM_ID).build();
        Game InProgressGame = Game.builder()
                .id(GAME_ID)
                .homeTeamScore(1)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .awayTeamScore(2)
                .build();
        when(boardService.getInProgressGame(GAME_ID)).thenReturn(InProgressGame);
        mockMvc.perform(get("/update-in-progress-game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("updateGame"))
                .andExpect(view().name("update_game.html"));

        verify(boardService, times(1)).getInProgressGame(GAME_ID);
    }

    @Test
    void ShowErrorPage_whenAttemptToDisplayUpdateGameForGameWhichNotExists() throws Exception {
        doThrow(new NoSuchElementException("Game not found with Id: " + GAME_ID))
                .when(boardService).getInProgressGame(GAME_ID);

        mockMvc.perform(get("/update-in-progress-game/" + GAME_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("error-page"))
                .andExpect(model().attribute("errormsg", "Game not found with Id: " + GAME_ID));

        verify(boardService, times(1)).getInProgressGame(GAME_ID);
    }

//    @Test
//    void showUpdatedBoard_whenGameUpdatedWithCorrectNewScores() throws Exception {
//        mockMvc.perform(post("/update-in-progress-game/" + GAME_ID)
//                        .param("homeTeamScore", UPDATED_HOME_TEAM_SCORE.toString())
//                        .param("awayTeamScore", UPDATED_AWAY_TEAM_SCORE.toString())
//                )
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/"));
//
//        verify(boardService, times(1)).updateGame(GAME_ID, UPDATED_HOME_TEAM_SCORE, UPDATED_AWAY_TEAM_SCORE);
//    }

    @Test
    void shouldShowUpdateGameViewAgainWithErrors_whenNeAttemptToUpdateGameWithNegativeHomeTeamScore() throws Exception {
        mockMvc.perform(post("/update-in-progress-game/" + GAME_ID)
                        .param("id", String.valueOf(GAME_ID))
                        .param("homeTeamScore", "-1")
                        .param("awayTeamScore", "10")
                        .param("homeTeamName", "Poland")
                        .param("awayTeamName", "Greece")
                        .param("version", "1")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("update_game.html"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("updateGame", "homeTeamScore"));

        verify(boardService, never()).createNewGame(any(NewGameDto.class));
    }

    @Test
    void shouldShowUpdateGameViewAgainWithErrors_whenAttemptToUpdateGameWithNegativeAwayTeamScore() throws Exception {
        mockMvc.perform(post("/update-in-progress-game/" + GAME_ID)
                        .param("id", String.valueOf(GAME_ID))
                        .param("homeTeamScore", "10")
                        .param("awayTeamScore", "-1")
                        .param("homeTeamName", "Poland")
                        .param("awayTeamName", "Greece")
                        .param("version", "1")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("update_game.html"))
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors("updateGame", "awayTeamScore"));

        verify(boardService, never()).createNewGame(any(NewGameDto.class));
    }
    // endregion

    // region Finish game
    @Test
    void showUpdatedBoardWithoutFinishedGame_whenGameFinishedCorrectly() throws Exception {
        mockMvc.perform(post("/finish-in-progress-game/" + GAME_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        verify(boardService, times(1)).finishGame(GAME_ID);
    }
    // endregion

    // region helpers
    private static Game createGame() {
        Team homeTeam = Team.builder().name("Poland").build();
        Team awayTeam = Team.builder().name("England").build();
        return Game.builder()
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .homeTeamScore(0)
                .awayTeamScore(0)
                .id(GAME_ID)
                .gameStatus(GameStatus.IN_PROGRESS).build();
    }
    //endregion
}