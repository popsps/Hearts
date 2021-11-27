package edu.gmu.server.service;

import edu.gmu.server.entity.User;
import edu.gmu.server.entity.UserInfo;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.repository.UserInfoRepository;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {
  private final UserRepository userRepository;
  private final UserInfoRepository userInfoRepository;

  @Autowired
  public UserService(UserRepository userRepository, UserInfoRepository userInfoRepository) {
    this.userRepository = userRepository;
    this.userInfoRepository = userInfoRepository;
  }

  @Transactional
  public Page<UserInfo> getAllUsers(int page, int limit, Map<String, String> filters) {
    Pageable pageable = PageRequest.of(page, limit);
    return this.userInfoRepository.findAll(pageable);
  }

  @Transactional
  public List<UserInfo> getAllUsers() {
    Sort sort = Sort.by("stats.win").descending()
      .and(Sort.by("stats.lost").ascending());
    return this.userInfoRepository.findAll(sort);
  }

  @Transactional
  public void uploadProfilePicture(UserDetails principal, MultipartFile picture) throws IOException, SQLException {
    if (principal != null) {
      log.info("principal {} attempts to upload a profile picture", principal.getUsername());
      User currentUser = this.userRepository.findByUsername(principal.getUsername())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, ""));
      log.info("Picture info: {}, {}, {}, {}", picture.getName(), picture.getSize(),
        picture.getBytes().length, picture.getOriginalFilename());
      Blob picBlob = new SerialBlob(picture.getBytes());
      currentUser.setProfilePicture(picBlob);
    } else {
      log.info("Attempt access to profile picture with bad credentials");
      throw new HeartsBadCredentialsException("Bad credentials provided");
    }
  }

  @Transactional
  public byte[] getProfilePicture(UserDetails principal)
    throws HeartsBadCredentialsException, HeartsResourceNotFoundException {
    try {
      if (principal != null) {
        log.info("principal {} attempts to get profile picture", principal.getUsername());
        User currentUser = this.userRepository.findByUsername(principal.getUsername())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Credentials"));
        int len = (int) currentUser.getProfilePicture().length();
        return currentUser.getProfilePicture().getBytes(1, len);
      } else {
        throw new BadCredentialsException("Bad Credentials provided");
      }
    } catch (BadCredentialsException e) {
      log.info("Attempt access to profile picture with bad credentials");
      throw new HeartsBadCredentialsException("Bad Credentials provided");
    } catch (NullPointerException e) {
      throw new HeartsResourceNotFoundException("A profile picture not found");
    } catch (Exception e) {
      throw new HeartsResourceNotFoundException("Operation failed");
    }
  }
}
