package kmichalski.scoreboard.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameDto {
    private long id;
    @Min(value = 0, message = "Home team score must be at least 0")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Home team score must be an integer")
    private int homeTeamScore;
    @Min(value = 0, message = "Away team score must be at least 0")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Away team score must be an integer")
    private int awayTeamScore;
}
