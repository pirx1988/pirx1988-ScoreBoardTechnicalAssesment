package kmichalski.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "games")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@NamedEntityGraph(
        name = "game-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "homeTeam", subgraph = "team-subgraph"),
                @NamedAttributeNode(value = "awayTeam", subgraph = "team-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "team-subgraph",
                        attributeNodes = { // Add attributes you want to fetch eagerly
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("name")
                        }
                )
        }
)
@Builder
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column
    private Integer homeTeamScore;

    @Column
    private Integer awayTeamScore;

    @Enumerated(EnumType.STRING)
    @Column
    private GameStatus gameStatus;
}
