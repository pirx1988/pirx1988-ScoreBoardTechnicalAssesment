package kmichalski.scoreboard.model;

import jakarta.persistence.*;
@Entity
@Table(name = "games")
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name="away_team_id", nullable = false)
    private Team awayTeam;

    @Enumerated(EnumType.STRING)
    private  GameStatus gameStatus;
}
