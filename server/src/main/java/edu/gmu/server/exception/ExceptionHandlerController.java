package edu.gmu.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

  @ExceptionHandler(MultipartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpErrorResponse handleMultipartException(MultipartException ex) {
    log.error("Multipart error. {}", ex.getMessage());
    log.debug("Multipart error.", ex);
    return new HttpErrorResponse(HttpStatus.BAD_REQUEST, "Current request is rejected because it is not supported");
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpErrorResponse handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    log.error("Multipart error. {}", ex.getMessage());
    log.debug("Multipart error.", ex);
    return new HttpErrorResponse(HttpStatus.BAD_REQUEST, "the request was rejected because its size exceeds the allowed maximum");
  }

  @ExceptionHandler(SizeLimitExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpErrorResponse handleSizeLimitExceededException(SizeLimitExceededException ex) {
    log.error("Request exceeded maximum size. {}", ex.getMessage());
    log.debug("Request exceeded maximum size.", ex);
    return new HttpErrorResponse(HttpStatus.BAD_REQUEST, "the request was rejected because its size exceeds the allowed maximum");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    log.error("MethodArgumentNotValidException: {}", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    log.debug("MethodArgumentNotValidException: ", ex);
    return new HttpErrorResponse(HttpStatus.BAD_REQUEST, ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public HttpErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    log.error("MethodArgumentNotValidException: {}", ex.getMessage());
    log.debug("MethodArgumentNotValidException: ", ex);
    return new HttpErrorResponse(HttpStatus.BAD_REQUEST, "Invalid card provided");
  }

}
