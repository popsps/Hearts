package edu.gmu.server.service;

import edu.gmu.server.entity.User;
import edu.gmu.server.entity.UserInfo;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.exception.HeartsUserNotFoundException;
import edu.gmu.server.repository.UserInfoRepository;
import edu.gmu.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;

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
  public Optional<byte[]> getProfilePicture(String username)
    throws HeartsResourceNotFoundException {
    try {
      User user = this.userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this resource"));
      if (user.getProfilePicture() != null) {
        int len = (int) user.getProfilePicture().length();
        return Optional.of(user.getProfilePicture().getBytes(1, len));
      } else {
        log.warn("A profile picture does not exist for user {}", user.getUsername());
        return Optional.empty();
      }
    } catch (Exception e) {
      throw new HeartsResourceNotFoundException("Operation failed");
    }
  }

  public Optional<User> getUserInfo(String username) {
    return this.userRepository.findByUsername(username);
  }
}
