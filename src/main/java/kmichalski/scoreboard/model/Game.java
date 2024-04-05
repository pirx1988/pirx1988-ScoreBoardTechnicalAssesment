package kmichalski.scoreboard.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "games")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraph(
        name = "game-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "homeTeam", subgraph = "team-subgraph"),
                @NamedAttributeNode(value = "awayTeam", subgraph = "team-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "team-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("name") // Add other attributes you want to fetch eagerly
                        }
                )
        }
)
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column
    private int homeTeamScore;

    @Column
    private int awayTeamScore;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;
}
