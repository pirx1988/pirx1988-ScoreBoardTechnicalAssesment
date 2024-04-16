package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.exception.ImproperStatusGameException;
import kmichalski.scoreboard.exception.IncorrectTotalScoreFormatException;
import kmichalski.scoreboard.exception.NegativeTotalScoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionController {

    /*
    @ExceptionHandler will register the given method for a given
    exception type, so that ControllerAdvice can invoke this method
    logic if a given exception type is thrown inside the web application.
    * */
    @ExceptionHandler(value = {ImproperStatusGameException.class})
    public ModelAndView improperGameStatusExceptionHandler(Exception exception) {
        return createAndSetupModelAndView(exception);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ModelAndView elementNotFoundExceptionHandler(Exception exception) {
        return createAndSetupModelAndView(exception);
    }


    @ExceptionHandler(value={IncorrectTotalScoreFormatException.class})
    public ModelAndView handleIncorrectTotalScoreFormatException(IncorrectTotalScoreFormatException exception) {
        return createAndSetupModelAndView(exception);
    }

    @ExceptionHandler(value={NegativeTotalScoreException.class})
    public ModelAndView handleNegativeValueOfTotalScoreException(NegativeTotalScoreException exception) {
        return createAndSetupModelAndView(exception);
    }

    @ExceptionHandler(value={ObjectOptimisticLockingFailureException.class})
    public ModelAndView handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException exception) {
        return createAndSetupModelAndView(exception);
    }

    // region helpers
    private static ModelAndView createAndSetupModelAndView(Exception exception) {
        ModelAndView errorPage = new ModelAndView();
        errorPage.setViewName("error-page");
        errorPage.addObject("errormsg", exception.getMessage());
        return errorPage;
    }
    // endregion
}
