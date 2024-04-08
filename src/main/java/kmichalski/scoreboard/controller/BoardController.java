package kmichalski.scoreboard.controller;

import jakarta.validation.Valid;
import kmichalski.scoreboard.dto.GameDto;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.GameStatus;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.service.BoardService;
import kmichalski.scoreboard.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final TeamService teamService;

    //region Get mapping
    @GetMapping(value={"","/","/board"})
    public String displayBoardPage (Model model) {
        List<Game> unfinishedGames =  boardService.getAllUnfinishedGames();
        model.addAttribute("unfinishedGames", unfinishedGames);
        return "board.html";
    }

    @GetMapping("/create-new-game")
    public String displayCreateNewGame(Model model) {
        fetchAllTeams(model);
        model.addAttribute("newGame", new GameDto());
        return "create_new_game.html";
    }
    //endregion

    //region Post mapping

    @PostMapping("/start-game/{gameId}")
    public String startGame(@PathVariable long gameId) {
        boardService.startNewGame(gameId);
        return "redirect:/"; // Redirect to board view: after starting the game
    }

    @PostMapping("/create-new-game")
    public String createNewGame(@Valid @ModelAttribute("newGame") GameDto game, Errors errors, RedirectAttributes redirectAttributes, Model model) {
        if (errors.hasErrors()) {
            log.error("Create new game form validation failed due to: " + errors);
            fetchAllTeams(model);
            return "create_new_game.html";
        }
        boardService.createNewGame(game);
        return "redirect:/";
    }

    private void fetchAllTeams(Model model) {
        List<Team> allTeams = teamService.getAllTeams();
        model.addAttribute("allTeams", allTeams);
    }
    //endregion
}
