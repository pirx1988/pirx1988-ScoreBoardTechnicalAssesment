package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.repostiory.GameRepository;
import kmichalski.scoreboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @RequestMapping(value={"","/","/board"})
    public String displayBoardPage (Model model) {
        List<Game> unfinishedGames =  boardService.getAllUnfinishedGames();
        model.addAttribute("unfinishedGames", unfinishedGames);
        return "board.html";
    }

    @PostMapping("/start-match/{matchId}")
    public String startMatch(@PathVariable long matchId) {
        boardService.startNewGame(matchId);
        return "redirect:/"; // Redirect to board view: after starting the match
    }
}
