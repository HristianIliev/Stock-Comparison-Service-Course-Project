package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UsersController {

  @Autowired
  private UsersService usersService;

  @PostMapping("/api/register")
  public ResponseEntity register(@RequestBody User user) {
    if (usersService.loadUserByUsername(user.getUsername()) != null) {
      return new ResponseEntity(HttpStatus.CONFLICT);
    }

    if (user.getEmail() == null || user.getEmail().trim().isEmpty() || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    usersService.save(user);

    return new ResponseEntity(HttpStatus.OK);
  }

}
