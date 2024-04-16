package kmichalski.scoreboard.controller;

import jakarta.validation.Valid;
import kmichalski.scoreboard.dto.NewGameDto;
import kmichalski.scoreboard.dto.UpdateGameDto;
import kmichalski.scoreboard.exception.IncorrectTotalScoreFormatException;
import kmichalski.scoreboard.exception.NegativeTotalScoreException;
import kmichalski.scoreboard.mapper.GameDtoMapper;
import kmichalski.scoreboard.model.Game;
import kmichalski.scoreboard.model.Team;
import kmichalski.scoreboard.service.BoardServiceImpl;
import kmichalski.scoreboard.service.TeamServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardServiceImpl boardService;
    private final TeamServiceImpl teamServiceImpl;
    private final GameDtoMapper gameDtoMapper;

    //region Board
    @GetMapping(value = {"", "/", "/board"})
    public String displayBoardPage(
            Model model,
            @RequestParam(name = "total_score", required = false) String totalScoreParam
    ) {
        Integer totalScore = Optional.ofNullable(totalScoreParam)
                .map(this::parseTotalScoreInteger)
                .orElse(null);

        // Apply positive total score validation to ensure totalScore is positive when totalScore is not null
        if (totalScore != null) {
            validateNoNegativeTotalScore(totalScore);
        }

        List<Game> unfinishedGames = Optional.ofNullable(totalScore)
                .map(boardService::getGamesByTotalScore)
                .orElseGet(boardService::getAllUnfinishedGames);

        model.addAttribute("unfinishedGames", unfinishedGames);
        return "board.html";
    }
    //endregion

    //region Create new game
    @GetMapping("/create-new-game")
    public String displayCreateNewGame(Model model) {
        fetchAllTeams(model);
        model.addAttribute("newGame", new NewGameDto());
        return "create_new_game.html";
    }

    @PostMapping("/create-new-game")
    public String createNewGame(@Valid @ModelAttribute("newGame") NewGameDto game, Errors errors, Model model) {
        if (errors.hasErrors()) {
            log.error("Create new game form validation failed due to: " + errors);
            fetchAllTeams(model);
            return "create_new_game.html";
        }
        boardService.createNewGame(game);
        return "redirect:/";
    }
    //endregion

    //region Start new game
    @PostMapping("/start-game/{gameId}")
    public String startGame(@PathVariable long gameId) {
        boardService.startNewGame(gameId);
        return "redirect:/"; // Redirect to board view: after starting the game
    }

    //endregion

    // region Update game
    @GetMapping("/update-in-progress-game/{gameId}")
    public String displayUpdateGame(Model model, @PathVariable long gameId) {
        Game inProgressGame = boardService.getInProgressGame(gameId);
        UpdateGameDto updateGameDto = gameDtoMapper.convertGameToUpdateGameDto(inProgressGame);
        model.addAttribute("updateGame", updateGameDto);
        model.addAttribute("homeTeamName", inProgressGame.getHomeTeam().getName());
        model.addAttribute("awayTeamName", inProgressGame.getAwayTeam().getName());
        return "update_game.html";
    }

    @PostMapping("/update-in-progress-game/{gameId}")
    public String updateInProgressGame(@Valid @ModelAttribute("updateGame") UpdateGameDto updatedGame, Errors errors,
                                       @PathVariable Long gameId) {
        if (errors.hasErrors()) {
            log.error("Updated new game form validation failed due to: " + errors);
            return "update_game.html";
        }
        boardService.updateGame(updatedGame);
        return "redirect:/";
    }
    //endregion

    //region Finish game
    @PostMapping("/finish-in-progress-game/{gameId}")
    public String finishInProgressGame(@PathVariable Long gameId) {
        boardService.finishGame(gameId);
        return "redirect:/";
    }
    //endregion

    // region helpers
    private void validateNoNegativeTotalScore(Integer totalScore) {
        if (totalScore < 0) {
            throw new NegativeTotalScoreException("Total score cannot be negative number. Passed value: " + totalScore);
        }
    }

    private void fetchAllTeams(Model model) {
        List<Team> allTeams = teamServiceImpl.getAllTeams();
        model.addAttribute("allTeams", allTeams);
    }

    private Integer parseTotalScoreInteger(String totalScore) {
        try {
            return Integer.parseInt(totalScore);
        } catch (NumberFormatException e) {
            throw new IncorrectTotalScoreFormatException("Total score must be in a valid format number. Passed value: " + totalScore);
        }
    }
    //endregion

}
