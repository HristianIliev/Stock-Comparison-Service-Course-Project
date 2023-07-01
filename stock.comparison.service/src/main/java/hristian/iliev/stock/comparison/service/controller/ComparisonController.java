package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.ComparisonCalculations;
import hristian.iliev.stock.comparison.service.comparison.entity.Tag;
import hristian.iliev.stock.comparison.service.stocks.StockQuoteService;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class ComparisonController {

  private UsersService usersService;

  private ComparisonService comparisonService;

  private StockQuoteService stockQuoteService;

  @GetMapping("/users/{username}/comparisons")
  public String userComparisons(@PathVariable("username") String username, @RequestParam(value = "periods", required = false, defaultValue = "200") int periods, Model model) {
    System.out.println("Retrieving comparisons for " + username + " for " + periods + " periods");
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return "error";
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    List<ComparisonCalculations> comparisonCalculations = new ArrayList<>();
    for (Comparison comparison : comparisons) {
      comparisonCalculations.add(stockQuoteService.calculateComparisonData(comparison, periods));
    }

    model.addAttribute("comparisons", comparisonCalculations);

    List<Tag> tags = comparisonService.retrieveTagsOfUser(user.getId());

    model.addAttribute("tags", tags);
    model.addAttribute("dashboards", user.getDashboards());
    model.addAttribute("username", username);

    return "home";
  }

  @GetMapping("/api/users/{username}/comparisons/names")
  public ResponseEntity<List<Comparison>> userComparisonNames(@PathVariable String username) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    return ResponseEntity.ok(comparisons);
  }

  @PostMapping("/api/users/{username}/comparisons")
  public ResponseEntity addStockComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    System.out.println("Adding a new comparison to user: " + username + " " + firstStockName + ":" + secondStockName);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    if (stockQuoteService.quotesForStockWithNameDoNotExist(firstStockName) || stockQuoteService.quotesForStockWithNameDoNotExist(secondStockName)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
      }
    }

    comparisonService.addComparisonToUser(user, firstStockName, secondStockName);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/comparisons")
  public ResponseEntity deleteStockComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.deleteComparison(user, firstStockName, secondStockName);
      }
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/comparisons/tags")
  public ResponseEntity addTagForComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName, @RequestBody Tag tag) {
    System.out.println(tag);

    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.tagComparison(user.getId(), firstStockName, secondStockName, tag);

        break;
      }
    }

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/api/users/{username}/comparisons/tags")
  public ResponseEntity deleteTagForComparison(@PathVariable String username, @RequestParam("firstStock") String firstStockName, @RequestParam("secondStock") String secondStockName) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());
    for (Comparison comparison : comparisons) {
      if (comparison.getFirstStockName().equals(firstStockName) && comparison.getSecondStockName().equals(secondStockName)) {
        comparisonService.deleteTagOf(comparison);

        break;
      }
    }

    return ResponseEntity.ok().build();
  }

}
