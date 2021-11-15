package edu.gmu.server.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class HttpErrorResponse {
//  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;

  public HttpErrorResponse(HttpStatus httpStatus, String message) {
    this.status = httpStatus.value();
    this.error = httpStatus.name();
    this.message = message;
    String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    this.timestamp = LocalDateTime.parse(now, DateTimeFormatter.ISO_DATE_TIME);
  }
}
