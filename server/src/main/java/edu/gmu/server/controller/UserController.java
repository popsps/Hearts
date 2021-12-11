package edu.gmu.server.controller;

import edu.gmu.server.entity.User;
import edu.gmu.server.entity.UserInfo;
import edu.gmu.server.exception.HeartsBadCredentialsException;
import edu.gmu.server.exception.HeartsException;
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

  @GetMapping("/user-info")
  public User getUserInfo(@AuthenticationPrincipal UserDetails currentUser) {
    String username = currentUser.getUsername();
    return this.userService.getUserInfo(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not enough privileges to access this resource"));
  }

  @PostMapping(value = "/profile-pic")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void uploadProfilePicture(@RequestParam(name = "file", required = true) MultipartFile picture,
                                   @AuthenticationPrincipal UserDetails principal) {
    try {
      if (picture.getContentType().equals("image/jpeg") ||
        picture.getContentType().equals("image/jpg") ||
        picture.getContentType().equals("image/png")) {
        this.userService.uploadProfilePicture(principal, picture);
      } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image type is not supported");
      }
    } catch (HeartsBadCredentialsException e) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bad Credentials");
    } catch (IOException | SQLException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong processing your request");
    }
  }

  @GetMapping(value = "/profile-pic", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] getProfilePicture(@AuthenticationPrincipal UserDetails principal, HttpServletResponse response) {
    response.setHeader("Content-disposition", "attachment; filename=profile.jpeg");
    String username = principal.getUsername();
    return this.userService.getProfilePicture(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "A Profile picture not found"));
  }

  @GetMapping(value = "/profile-pic/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] getProfilePictureByUsername(HttpServletResponse response, @PathVariable String username) {
    response.setHeader("Content-disposition", "attachment; filename=profile.jpeg");
    return this.userService.getProfilePicture(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "A Profile picture not found"));
  }
}
