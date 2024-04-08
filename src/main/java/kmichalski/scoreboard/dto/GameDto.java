package kmichalski.scoreboard.dto;

import kmichalski.scoreboard.annotation.CheckDifferentTeams;
import lombok.*;

@Setter
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@CheckDifferentTeams
public class GameDto {
    private Long homeTeamId;
    private Long awayTeamId;

}
