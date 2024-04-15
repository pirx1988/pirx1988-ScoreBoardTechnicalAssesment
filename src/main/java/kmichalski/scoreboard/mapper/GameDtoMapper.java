package kmichalski.scoreboard.mapper;

import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.model.Game;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameDtoMapper {

    private final ModelMapper modelMapper;
    public UpdateGameDto convertGameToUpdateGameDto(Game game) {
        UpdateGameDto updateGameDto = modelMapper.map(game, UpdateGameDto.class);
        return updateGameDto;
    }

    public Game convertUpdateGameDtoToGame(UpdateGameDto updateGameDto) {
        Game updatedGame = modelMapper.map(updateGameDto, Game.class);
        return updatedGame;
    }
}
