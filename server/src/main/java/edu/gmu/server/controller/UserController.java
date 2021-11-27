package edu.gmu.server.controller;

import edu.gmu.server.entity.User;
import edu.gmu.server.entity.UserInfo;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsResourceNotFoundException;
import edu.gmu.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

//  @GetMapping
//  public Page<UserInfo> getAllUsers(@RequestParam(defaultValue = "0", required = false) int page,
//                                    @RequestParam(defaultValue = "20", required = false) int limit,
//                                    @RequestParam(required = false) Map<String, String> filters) {
//    return this.userService.getAllUsers(page, limit, filters);
//  }
  @GetMapping
  public List<UserInfo> getAllUsers() {
    return this.userService.getAllUsers();
  }

  @PostMapping("/profile-pic")
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadProfilePicture(@RequestParam(name = "file", required = true) MultipartFile picture,
                                   @AuthenticationPrincipal UserDetails principal) {
    try {
      this.userService.uploadProfilePicture(principal, picture);
    } catch (HeartsBadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bad Credentials");
    } catch (IOException | SQLException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong processing your request");
    }
  }

  @GetMapping(value = "/profile-pic", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] getProfilePicture(@AuthenticationPrincipal UserDetails principal,
                                  HttpServletResponse response) {
    try {
      response.setHeader("Content-disposition", "attachment; filename=profile.jpeg");
      return this.userService.getProfilePicture(principal);
    } catch (HeartsBadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bad Credentials");
    } catch (HeartsResourceNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A Profile picture not found");
    }
  }
}
