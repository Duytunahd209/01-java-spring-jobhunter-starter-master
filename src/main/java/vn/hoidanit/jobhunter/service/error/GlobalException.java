package vn.hoidanit.jobhunter.service.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(IdInvaliException.class)
    public ResponseEntity<String> handleAllException(IdInvaliException invaliException) {
        return ResponseEntity.badRequest().body(invaliException.getMessage());
    }

}
