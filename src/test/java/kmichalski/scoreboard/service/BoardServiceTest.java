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

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    private static final long GAME_ID = 123L;
    private static final int NEW_HOME_TEAM_SCORE = 5;
    private static final int NEW_AWAY_TEAM_SCORE = 4;
    @Mock
    GameRepository gameRepository;

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

        verify(gameRepository, times(1)).save(newGameArgumentCaptor.capture());

        Game newGame = newGameArgumentCaptor.getValue();
        assertThat(newGame.getGameStatus()).isEqualTo(GameStatus.NEW);
        assertThat(newGame.getHomeTeam()).isEqualTo(savedHomeTeamMock);
        assertThat(newGame.getAwayTeam()).isEqualTo(savedAwayTeamMock);
        assertThat(newGame.getHomeTeamScore()).isNull();
        assertThat(newGame.getAwayTeamScore()).isNull();
    }

    @Test
    void shouldCorrectlyUpdateGame_whenChangedFromNewToStarted() {
        Game game = Mockito.spy(Game.builder().build());

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        // Act
        service.startNewGame(GAME_ID);

        ArgumentCaptor<Game> startedGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository, times(1)).save(startedGameArgumentCaptor.capture());

        Game startedGame = startedGameArgumentCaptor.getValue();
        assertThat(startedGame.getGameStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(startedGame.getHomeTeamScore()).isEqualTo(0);
        assertThat(startedGame.getAwayTeamScore()).isEqualTo(0);
    }

    @Test
    void shouldCorrectlyUpdateAlreadyStartedGame() {
        Game game = Mockito.spy(Game.builder().gameStatus(GameStatus.IN_PROGRESS).build());

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        // Act
        service.updateGame(GAME_ID, NEW_HOME_TEAM_SCORE, NEW_AWAY_TEAM_SCORE);

        ArgumentCaptor<Game> updatedGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository, times(1)).save(updatedGameArgumentCaptor.capture());

        Game updatedGame = updatedGameArgumentCaptor.getValue();
        assertThat(updatedGame.getGameStatus()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(updatedGame.getHomeTeamScore()).isEqualTo(NEW_HOME_TEAM_SCORE);
        assertThat(updatedGame.getAwayTeamScore()).isEqualTo(NEW_AWAY_TEAM_SCORE);
    }

    @Test
    void shouldCorrectlyFinishGame() {
        Game game = Mockito.spy(Game.builder().gameStatus(GameStatus.IN_PROGRESS).build());

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        // Act
        service.finishGame(GAME_ID);

        ArgumentCaptor<Game> finisheddGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository, times(1)).save(finisheddGameArgumentCaptor.capture());

        Game updatedGame = finisheddGameArgumentCaptor.getValue();
        assertThat(updatedGame.getGameStatus()).isEqualTo(GameStatus.FINISHED);
    }
}