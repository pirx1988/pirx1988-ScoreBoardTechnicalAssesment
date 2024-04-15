package kmichalski.scoreboard.service;

import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.repostiory.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}
