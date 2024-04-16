package kmichalski.scoreboard.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameDto {
    @NotNull
    @Min(value = 1, message = "Version has to have value at most 1")
    private Integer version;
    @NotNull
    @Min(value = 1, message = "Id has to have value at most 1")
    private long id;
    @Min(value = 0, message = "Home team score must be at least 0")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Home team score must be an integer")
    private Integer homeTeamScore;
    @Min(value = 0, message = "Away team score must be at least 0")
    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Away team score must be an integer")
    private Integer awayTeamScore;
}
