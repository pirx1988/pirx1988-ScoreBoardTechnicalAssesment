package kmichalski.scoreboard.exception;

public class ImproperStatusGameException extends RuntimeException {
    public ImproperStatusGameException(String message) {
        super(message);
    }
    public ImproperStatusGameException(Exception exception) {
        super(exception);
    }
}
