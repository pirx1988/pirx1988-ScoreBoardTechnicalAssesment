package kmichalski.scoreboard.service;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    GameRepository repository;

    @Mock
    TeamRepository teamRepository;

    @InjectMocks
    BoardService service;

    @Test
    void shouldCreateNewGame_whenCreateNewGameInvokedWithTwoDifferentTeams() {
        Team homeTeamMock = Mockito.mock(Team.class);
        Team awayTeamMock = Mockito.mock(Team.class);

        Team savedHomeTeamMock = Mockito.mock(Team.class);
        Team savedAwayTeamMock = Mockito.mock(Team.class);

        when(teamRepository.save(homeTeamMock)).thenReturn(savedHomeTeamMock);
        when(teamRepository.save(awayTeamMock)).thenReturn(savedAwayTeamMock);

        // Act
        service.createNewGame(homeTeamMock, awayTeamMock);

        ArgumentCaptor<Game> newGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(repository, times(1)).save(newGameArgumentCaptor.capture());

        Game newGame = newGameArgumentCaptor.getValue();
        assertThat(newGame.getGameStatus()).isEqualTo(GameStatus.NEW);
        assertThat(newGame.getHomeTeam()).isEqualTo(savedHomeTeamMock);
        assertThat(newGame.getAwayTeam()).isEqualTo(savedAwayTeamMock);
        assertThat(newGame.getHomeTeamScore()).isNull();
        assertThat(newGame.getAwayTeamScore()).isNull();
    }
}