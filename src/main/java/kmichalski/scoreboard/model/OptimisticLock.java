package kmichalski.scoreboard.model;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class OptimisticLock extends BaseEntity {
    @Version
    private Integer version;
}
