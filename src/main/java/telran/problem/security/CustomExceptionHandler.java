package telran.problem.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import telran.problem.dto.exceptions.ExceptionDto;
import telran.problem.dto.exceptions.ProblemNotFoundException;
import telran.problem.dto.exceptions.WrongAuthorityException;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(ProblemNotFoundException.class)
    public ResponseEntity<Object> handleProfileExistsException(ProblemNotFoundException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.BAD_REQUEST.value(), "Bad Request");
        exceptionDto.setMessage("Problem is not exists.");
        exceptionDto.setPath("/problem/*");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }

    @ExceptionHandler(WrongAuthorityException.class)
    public ResponseEntity<Object> handleProfileExistsException(WrongAuthorityException ex) {
        ExceptionDto exceptionDto = new ExceptionDto(HttpStatus.FORBIDDEN.value(), "Forbidden");
        exceptionDto.setMessage("You have no permissions to edit that problem.");
        exceptionDto.setPath("/problem/*");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionDto);
    }
}
