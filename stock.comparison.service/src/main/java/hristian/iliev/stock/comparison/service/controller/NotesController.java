package hristian.iliev.stock.comparison.service.controller;

import hristian.iliev.stock.comparison.service.comparison.ComparisonService;
import hristian.iliev.stock.comparison.service.comparison.NoteService;
import hristian.iliev.stock.comparison.service.comparison.entity.Comparison;
import hristian.iliev.stock.comparison.service.comparison.entity.Note;
import hristian.iliev.stock.comparison.service.users.UsersService;
import hristian.iliev.stock.comparison.service.users.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class NotesController {

  private UsersService usersService;

  private ComparisonService comparisonService;

  private NoteService noteService;

  @GetMapping("/users/{username}/notes")
  public String notes(@PathVariable("username") String username, Model model) {
    User user = usersService.retrieveUserByUsername(username);

    if (user == null) {
      return "error";
    }

    List<Comparison> comparisons = comparisonService.retrieveComparisonsByUser(user.getId());

    Map<String, List<Note>> notesGroupedByComparison = new HashMap<>();

    for (Comparison comparison : comparisons) {
      if (comparison.getNotes().isEmpty()) {
        continue;
      }

      String comparisonKey = comparison.getFirstStockName() + "-" + comparison.getSecondStockName();

      notesGroupedByComparison.put(comparisonKey, comparison.getNotes());
    }

    model.addAttribute("groupedNotes", notesGroupedByComparison);

    return "notes";
  }

  @DeleteMapping("/api/users/{username}/notes/{noteId}")
  public ResponseEntity deleteNote(@PathVariable("username") String username, @PathVariable("noteId") int noteId) {
    noteService.deleteNote(noteId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/api/users/{username}/notes")
  public ResponseEntity createNote(@PathVariable("username") String username, @RequestBody Note note) {
    noteService.createNote(note);

    return ResponseEntity.ok().build();
  }

}
