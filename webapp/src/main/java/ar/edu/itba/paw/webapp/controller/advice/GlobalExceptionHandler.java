package ar.edu.itba.paw.webapp.controller.advice;

import ar.edu.itba.paw.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /* Routes that do not have a mapping fall here */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFound(){
        return new ModelAndView("error/notFound");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleUserNotFound(){
        return new ModelAndView("error/notFound")
            .addObject("title", "User Not Found")
            .addObject("message","Looks like the user you were looking for has not registered yet, but soon they will!");
    }
}
