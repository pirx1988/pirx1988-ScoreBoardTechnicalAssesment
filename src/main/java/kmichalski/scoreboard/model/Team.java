package kmichalski.scoreboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
}
