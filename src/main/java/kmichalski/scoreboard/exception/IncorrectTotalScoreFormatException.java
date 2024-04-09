package kmichalski.scoreboard.exception;

public class IncorrectTotalScoreFormatException extends RuntimeException {
    public IncorrectTotalScoreFormatException(String message) {
        super(message);
    }

    public IncorrectTotalScoreFormatException(Exception exception) {
        super(exception);
    }
}
