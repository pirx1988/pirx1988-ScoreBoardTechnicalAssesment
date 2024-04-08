package kmichalski.scoreboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kmichalski.scoreboard.annotation.CheckDifferentTeams;
import kmichalski.scoreboard.dto.GameDto;
import kmichalski.scoreboard.model.Game;

public class DifferentTeamsValidator implements ConstraintValidator<CheckDifferentTeams, GameDto> {
    @Override
    public boolean isValid(GameDto game, ConstraintValidatorContext context) {
        return game.getHomeTeamId() != game.getAwayTeamId();
    }
}