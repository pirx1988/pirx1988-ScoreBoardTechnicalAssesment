package kmichalski.scoreboard.service;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.exception.NegativeTeamScoreException;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.repostiory.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    private static final long GAME_ID = 123L;
    private static final int NEW_HOME_TEAM_SCORE = 5;
    private static final int NEW_AWAY_TEAM_SCORE = 4;
    private static final Long HOME_TEAM_ID = 1L;
    private static final long AWAY_TEAM_ID = 2L;
    @Mock
    GameRepository gameRepository;
    @Mock
    TeamRepository teamRepository;
    @InjectMocks
    BoardService service;

    // region Create new game

    @Test
    void shouldProperlyCreateNewGame() {
        NewGameDto newgameDto = NewGameDto.builder()
                .homeTeamId(HOME_TEAM_ID)
                .awayTeamId(AWAY_TEAM_ID)
                .build();

        Team homeTeam = Mockito.mock(Team.class);
        Team awayTeam = Mockito.mock(Team.class);


        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.of(awayTeam));

        // Act
        service.createNewGame(newgameDto);

        ArgumentCaptor<Game> newGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository, times(1)).save(newGameArgumentCaptor.capture());

        Game newGame = newGameArgumentCaptor.getValue();
        assertThat(newGame.getGameStatus()).isEqualTo(GameStatus.NEW);
        assertThat(newGame.getHomeTeam()).isEqualTo(homeTeam);
        assertThat(newGame.getAwayTeam()).isEqualTo(awayTeam);
        assertThat(newGame.getHomeTeamScore()).isNull();
        assertThat(newGame.getAwayTeamScore()).isNull();
    }

    @Test
    void shouldThrowNoSuchElementException_whenAttemptToCreateGameWhilstHomeTeamNotExists() {
        NewGameDto newgameDto = NewGameDto.builder()
                .homeTeamId(HOME_TEAM_ID)
                .awayTeamId(AWAY_TEAM_ID)
                .build();

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> service.createNewGame(newgameDto));
        verify(teamRepository, times(1)).findById(HOME_TEAM_ID);
        verifyNoInteractions(gameRepository);
    }

    @Test
    void shouldThrowNoSuchElementException_whenAttemptToCreateGameWhilstAwayTeamNotExists() {
        NewGameDto newgameDto = NewGameDto.builder()
                .homeTeamId(HOME_TEAM_ID)
                .awayTeamId(AWAY_TEAM_ID)
                .build();

        Team homeTeam = Mockito.mock(Team.class);

        when(teamRepository.findById(HOME_TEAM_ID)).thenReturn(Optional.of(homeTeam));
        when(teamRepository.findById(AWAY_TEAM_ID)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> service.createNewGame(newgameDto));
        verify(teamRepository, times(1)).findById(HOME_TEAM_ID);
        verify(teamRepository, times(1)).findById(AWAY_TEAM_ID);
        verifyNoInteractions(gameRepository);
    }
    //endregion

    // region Start game
    @Test
    void shouldCorrectlyStartGame_whenChangedFromNewToStarted() {
        Game game = Mockito.spy(Game.builder().build());
        game.setGameStatus(GameStatus.NEW);

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
    void shouldThrowNoSuchElementException_whenAttemptToStartGameWhichNotExists() {

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> service.startNewGame(GAME_ID));
        verify(gameRepository, times(1)).findById(GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("startGameStatusProvider")
    void shouldThrowImproperStatusException_whenStarNewGameWithImproperStatus(String testCaseDescription, GameStatus gameStatus) {
        Game game = Mockito.spy(Game.builder().gameStatus(gameStatus).build());

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        // Act
        assertThrows(ImproperStatusGameException.class, () -> service.startNewGame(GAME_ID));
        verify(gameRepository, times(1)).findById(GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    private static Stream<Arguments> startGameStatusProvider() {
        return Stream.of(
                Arguments.of("Game with InProgress status", GameStatus.IN_PROGRESS),
                Arguments.of("Game with Finished status", GameStatus.FINISHED)
        );
    }
    // endregion

    // region Update game

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
    void shouldThrowException_whenAttemptToUpdateHomeTeamScoreWithNegativeValue() {
        // Act
        assertThrows(NegativeTeamScoreException.class, () -> service.updateGame(GAME_ID,-1,1));
        verify(gameRepository,never()).findById(GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void shouldThrowException_whenAttemptToUpdateAwayTeamScoreWithNegativeValue() {
        // Act
        assertThrows(NegativeTeamScoreException.class, () -> service.updateGame(GAME_ID,10,-1));
        verify(gameRepository,never()).findById(GAME_ID);
        verify(gameRepository, never()).save(any(Game.class));
    }

    //endregion

    // region Finish game

    @Test
    void shouldCorrectlyFinishGame() {
        Game game = Mockito.spy(Game.builder().gameStatus(GameStatus.IN_PROGRESS).build());

        when(game.getId()).thenReturn(GAME_ID);
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(game));

        // Act
        service.finishGame(GAME_ID);

        ArgumentCaptor<Game> finisheddGameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        verify(gameRepository, times(1)).save(finisheddGameArgumentCaptor.capture());

        Game updatedGame = finisheddGameArgumentCaptor.getValue();
        assertThat(updatedGame.getGameStatus()).isEqualTo(GameStatus.FINISHED);
    }
    @Test
    void shouldThrowNoSuchElementException_whenGameToFinishNotFound() {
        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> service.finishGame(GAME_ID));

        verify(gameRepository, never()).save(any(Game.class));
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] - {0}")
    @MethodSource("finishGameStatusProvider")
    void shouldThrowImproperStatusGameException_whenGameToFinishWitAnotherThanInProgressStatus(String testCaseDescription, GameStatus gameStatus) {
        Game toFinishGame = Mockito.spy(Game.builder().gameStatus(gameStatus).build());

        when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(toFinishGame));

        // Act
        assertThrows(ImproperStatusGameException.class, () -> service.finishGame(GAME_ID));
        verify(gameRepository, never()).save(any(Game.class));
    }

    private static Stream<Arguments> finishGameStatusProvider() {
        return Stream.of(
                Arguments.of("Game with New status", GameStatus.NEW),
                Arguments.of("Game with Finished status", GameStatus.FINISHED)
        );
    }
    //endregion

    //region Fetch in progress game
    @Test
    void shouldCorrectlyGetInProgressGame() {
        when(gameRepository.findByIdAndGameStatus(GAME_ID, GameStatus.IN_PROGRESS)).thenReturn(Optional.of(Mockito.mock(Game.class)));

        // Act
        service.getInProgressGame(GAME_ID);

        verify(gameRepository, times(1)).findByIdAndGameStatus(GAME_ID, GameStatus.IN_PROGRESS);
    }

    @Test
    void shouldThrowExceptionWhenAttemptToGetInProgressGameWhichNotExists() {
        when(gameRepository.findByIdAndGameStatus(GAME_ID, GameStatus.IN_PROGRESS)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> service.getInProgressGame(GAME_ID));

        verify(gameRepository, never()).save(any(Game.class));
    }
    // endregion
}