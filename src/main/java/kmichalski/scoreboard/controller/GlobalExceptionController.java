package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.exception.ImproperStatusGameException;
import lombok.extern.slf4j.Slf4j;
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
        ModelAndView errorPage = new ModelAndView();
        errorPage.setViewName("error-page");
        errorPage.addObject("errormsg", exception.getMessage());
        return errorPage;
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ModelAndView elementNotFoundExceptionHandler(Exception exception) {
        ModelAndView errorPage = new ModelAndView();
        errorPage.setViewName("error-page");
        errorPage.addObject("errormsg", exception.getMessage());
        return errorPage;
    }
}
