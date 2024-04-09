package kmichalski.scoreboard.exception;

public class NegativeTeamScoreException extends RuntimeException{
    public NegativeTeamScoreException(String message) {
        super(message);
    }

    public NegativeTeamScoreException(Exception exception) {
        super(exception);
    }
}
