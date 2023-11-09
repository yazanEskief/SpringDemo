package de.fhws.fiw.fds.springDemoApp.exception;

import de.fhws.fiw.fds.springDemoApp.util.Operation;
import de.fhws.fiw.fds.springDemoApp.util.Roles;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ExceptionEntity> exceptionHandler(Exception e) {

        ExceptionEntity exceptionEntity = new ExceptionEntity(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(exceptionEntity, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {LinkLocationToPersonNotAllowedException.class,
            UnsupportedUnlinkOperation.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionEntity> linkingExceptionHandler(Exception e, HttpServletRequest request) {

        ExceptionEntity exceptionEntity = new ExceptionEntity(
                e.getMessage(),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );

        if (e instanceof MethodArgumentTypeMismatchException exception) {
            if (exception.getRequiredType() == Operation.class) {
                String operation = request.getParameter("op");
                List<Operation> operations = Arrays.asList(Operation.values());
                exceptionEntity = new ExceptionEntity(
                        "operation " + operation + " is not recognized. Supported Operations: " +
                                operations.stream().map(Enum::toString).collect(Collectors.joining(", ")),
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now()
                );
            }

            if(exception.getRequiredType() == Roles.class) {
                exceptionEntity = new ExceptionEntity(
                        "Unrecognized Role: " + exception.getValue(),
                        HttpStatus.BAD_REQUEST,
                        LocalDateTime.now()
                );
            }
        }

        return new ResponseEntity<>(exceptionEntity, HttpStatus.BAD_REQUEST);
    }
}
