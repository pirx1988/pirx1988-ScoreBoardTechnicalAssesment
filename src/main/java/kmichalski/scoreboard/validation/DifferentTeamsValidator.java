package kmichalski.scoreboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kmichalski.scoreboard.annotation.CheckDifferentTeams;
import kmichalski.scoreboard.dto.NewGameDto;

public class DifferentTeamsValidator implements ConstraintValidator<CheckDifferentTeams, NewGameDto> {
    @Override
    public boolean isValid(NewGameDto game, ConstraintValidatorContext context) {
        return game.getHomeTeamId() != game.getAwayTeamId();
    }
}