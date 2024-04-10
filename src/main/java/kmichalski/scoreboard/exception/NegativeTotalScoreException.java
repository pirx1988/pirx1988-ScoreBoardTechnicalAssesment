package kmichalski.scoreboard.exception;

public class NegativeTotalScoreException extends RuntimeException {
    public NegativeTotalScoreException(String message) {
        super(message);
    }
    public NegativeTotalScoreException(Exception exception) {
        super(exception);
    }
}
