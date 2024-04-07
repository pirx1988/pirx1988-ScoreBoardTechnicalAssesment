package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.service.BoardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = {BoardController.class})
@ContextConfiguration(classes = BoardController.class) // solved problem with loading JPA into context
class BoardControllerTest {

    @MockBean
    private BoardService boardService;

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
}