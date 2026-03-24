package com.openapp.application.exception;

import com.openapp.application.dto.response.ApiResponse;
import com.openapp.application.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理器
 * 捕获并处理所有控制器层的异常，返回统一的错误响应格式
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     * 
     * @param ex 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        log.warn("Business exception: code={}, message={}", ex.getErrorCodeString(), ex.getFinalMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ex.getErrorCodeString())
                .message(ex.getFinalMessage())
                .details(buildDetailsList(ex.getDetails()))
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatus());
    }

    /**
     * 处理参数验证异常（@Valid）
     * 
     * @param ex 方法参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        
        List<Map<String, Object>> details = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("field", fieldError.getField());
            detail.put("rejectedValue", fieldError.getRejectedValue());
            detail.put("message", fieldError.getDefaultMessage());
            details.add(detail);
        }
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ApplicationErrorCode.VALIDATION_ERROR.getCode())
                .message(ApplicationErrorCode.VALIDATION_ERROR.getMessage())
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理约束违反异常（@ConstraintViolation）
     * 
     * @param ex 约束违反异常
     * @return 错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation exception: {}", ex.getMessage());
        
        List<Map<String, Object>> details = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("field", violation.getPropertyPath().toString());
            detail.put("rejectedValue", violation.getInvalidValue());
            detail.put("message", violation.getMessage());
            details.add(detail);
        }
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ApplicationErrorCode.VALIDATION_ERROR.getCode())
                .message(ApplicationErrorCode.VALIDATION_ERROR.getMessage())
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理通用异常
     * 
     * @param ex 通用异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception: ", ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ApplicationErrorCode.INTERNAL_ERROR.getCode())
                .message("系统内部错误，请稍后重试")
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理运行时异常
     * 
     * @param ex 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: ", ex);
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ApplicationErrorCode.INTERNAL_ERROR.getCode())
                .message("系统运行时错误，请稍后重试")
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理 IllegalArgumentException
     * 
     * @param ex 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument exception: {}", ex.getMessage());
        
        ErrorResponse response = ErrorResponse.builder()
                .code(ApplicationErrorCode.VALIDATION_ERROR.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 构建详情列表
     * 
     * @param detailsMap 详情 Map
     * @return 详情列表
     */
    private List<Map<String, Object>> buildDetailsList(Map<String, Object> detailsMap) {
        if (detailsMap == null || detailsMap.isEmpty()) {
            return null;
        }
        
        List<Map<String, Object>> details = new ArrayList<>();
        for (Map.Entry<String, Object> entry : detailsMap.entrySet()) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("key", entry.getKey());
            detail.put("value", entry.getValue());
            details.add(detail);
        }
        return details;
    }
}
