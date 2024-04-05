package kmichalski.scoreboard.controller;

import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.repostiory.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final GameRepository gameRepository;

    @RequestMapping(value={"","/","/board"})
    public String displayBoardPage (Model model) {
//        Game games =  gameRepository.findById(1L).orElseThrow();
        List<Game> gamesList =  gameRepository.findAll();
        return "board.html";
    }
}
