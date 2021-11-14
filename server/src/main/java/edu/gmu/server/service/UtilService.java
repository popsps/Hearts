package edu.gmu.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class UtilService {
  public String getCurrentDateTimeUTCString() {
    return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
  }

  public LocalDateTime getCurrentDateTimeUTC() {
    String now = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
    return LocalDateTime.parse(now, DateTimeFormatter.ISO_DATE_TIME);
  }
}
