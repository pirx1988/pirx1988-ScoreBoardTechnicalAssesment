package kmichalski.scoreboard.service;

import kmichalski.scoreboard.repostiory.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    TeamRepository teamRepository;

    @InjectMocks
    TeamServiceImpl service;
    @Test
    void shouldGetAllTeamsFromRepository() {

        // Act
        service.getAllTeams();

        verify(teamRepository, times(1)).findAll();
    }
}