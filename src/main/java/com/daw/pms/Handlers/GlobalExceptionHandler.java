package com.daw.pms.Handlers;

import com.daw.pms.DTO.Result;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public Result handleException(Exception e) {
    return Result.fail(e.getMessage() + "\n#0\t" + e.getStackTrace()[0].toString());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public Result handleException(MethodArgumentNotValidException e) {
    List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
    FieldError fieldError = fieldErrors.get(0);
    return Result.fail(fieldError.getDefaultMessage());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseBody
  public Result handleException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
    if (iterator.hasNext()) {
      ConstraintViolation<?> next = iterator.next();
      return Result.fail(next.getMessage());
    }
    return Result.fail("Something bad happened");
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseBody
  public Result handleException(AuthenticationException e) {
    return Result.fail("Login failed, please check your username and password.");
  }
  //  @ExceptionHandler(Exception.class)
  //  public ResponseEntity<String> handleException(Exception ex) {
  //    String errorMessage =
  //        Result.fail(ex.getMessage() + "\n#0\t" + ex.getStackTrace()[0].toString()).toString();
  //    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  //  }
}
