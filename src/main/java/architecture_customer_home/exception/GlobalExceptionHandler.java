package architecture_customer_home.exception;

import architecture_customer_home.config.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        MDC.put("errorCode", ex.getErrorCode().name());
        try {
            log.warn("Business rule violation [path={}]: {}", request.getRequestURI(), ex.getMessage());
        } finally {
            MDC.remove("errorCode");
        }

        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        ex.getHttpStatus().value(),
                        ex.getErrorCode(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        currentTraceId(),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        List<ErrorResponse.FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldValidationError(fe.getField(), fe.getDefaultMessage()))
                .toList();

        log.warn("Validation error [path={}]: {}", request.getRequestURI(), fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_ERROR,
                        "Uno o más campos son inválidos",
                        request.getRequestURI(),
                        currentTraceId(),
                        fieldErrors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        String traceId = currentTraceId();
        log.error("Unexpected error [traceId={}, path={}]", traceId, request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_ERROR,
                        "Ocurrió un error inesperado. Reporta este traceId a soporte.",
                        request.getRequestURI(),
                        traceId,
                        null
                ));
    }

    private String currentTraceId() {
        return MDC.get(CorrelationIdFilter.MDC_TRACE_ID_KEY);
    }
}