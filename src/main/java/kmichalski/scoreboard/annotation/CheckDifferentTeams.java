package kmichalski.scoreboard.annotation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import kmichalski.scoreboard.validation.DifferentTeamsValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DifferentTeamsValidator.class)
public @interface CheckDifferentTeams {
    String message() default "Home and away teams must be different";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
