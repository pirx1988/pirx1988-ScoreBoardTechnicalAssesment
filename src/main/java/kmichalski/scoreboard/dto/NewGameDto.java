package kmichalski.scoreboard.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kmichalski.scoreboard.annotation.CheckDifferentTeams;
import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@CheckDifferentTeams
public class NewGameDto {
    @NotNull
    @Min(value = 1, message = "Home Team not selected")
    private Long homeTeamId;
    @NotNull
    @Min(value = 1, message = "Away Team not selected")
    private Long awayTeamId;
}
