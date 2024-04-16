package kmichalski.scoreboard.config;

import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper gameModelMapper = new ModelMapper();
        gameModelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        gameModelMapper.typeMap(NewGameDto.class, Game.class)
                .addMappings(mapper -> {
                    mapper.map(src -> GameStatus.NEW, Game::setGameStatus);
                    mapper.map(src -> null, Game::setHomeTeamScore);
                    mapper.map(src -> null, Game::setAwayTeamScore);
                    mapper.map(NewGameDto::getHomeTeamId, (destination, value) -> destination.getHomeTeam().setId((Long) value));
                    mapper.map(NewGameDto::getAwayTeamId, (destination, value) -> destination.getAwayTeam().setId((Long) value));
                    mapper.map(src -> 1, (destination, value) -> destination.getHomeTeam().setVersion(1));
                    mapper.map(src -> 1, (destination, value) -> destination.getAwayTeam().setVersion(1));
                });
        return gameModelMapper;
    }

    private static void initializeTeamVersion(Team team) {
        team.setVersion(1);
    }
}
