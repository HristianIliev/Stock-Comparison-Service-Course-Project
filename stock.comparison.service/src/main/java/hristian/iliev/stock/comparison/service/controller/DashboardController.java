package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.dashboard.DashboardService;
import hristian.iliev.stock.comparison.service.dashboard.entity.Chart;
import hristian.iliev.stock.comparison.service.dashboard.entity.Dashboard;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;

@Controller
@AllArgsConstructor
public class DashboardController {

  private UsersService usersService;

  private DashboardService dashboardService;

  @PostMapping("/api/users/{username}/dashboards")
  public ResponseEntity addDashboard(@PathVariable String username, @RequestBody Dashboard dashboard) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    dashboard.setUser(user);

    dashboardService.saveDashboard(dashboard);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/dashboards/{dashboardId}")
  public ResponseEntity deleteDashboard(@PathVariable String username, @PathVariable int dashboardId) {
    dashboardService.deleteDashboard(new Long(dashboardId));

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/dashboards/{dashboardName}/charts")
  public ResponseEntity addChartToDashboard(@PathVariable String username, @PathVariable String dashboardName, @RequestBody Chart chart) {
    System.out.println(chart);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    try {
      dashboardService.addChartToDashboard(username, dashboardName, chart);
    } catch(InvalidParameterException exception) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/users/{username}/dashboards")
  public String userComparisons(@PathVariable("username") String username, Model model) {
    User user = usersService.retrieveUserByUsername(username);

    model.addAttribute("dashboards", user.getDashboards());
    model.addAttribute("username", username);

    return "dashboards";
  }

  @GetMapping("/users/{username}/dashboards/{dashboardId}")
  public String dashboardInformation(@PathVariable("username") String username, @PathVariable int dashboardId, Model model) {
    Dashboard dashboard = dashboardService.retrieveById(new Long(dashboardId));

    model.addAttribute("charts", dashboard.getCharts());

    return "charts";
  }

}
