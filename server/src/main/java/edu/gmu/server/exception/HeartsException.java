package edu.gmu.server.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartsException extends RuntimeException {
  public HeartsException(String message) {
    super(message);
    log.error("Hearts Exception; {}", message);
  }
}
