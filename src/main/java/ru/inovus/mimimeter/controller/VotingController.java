package ru.inovus.mimimeter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.inovus.mimimeter.controller.dto.VoteRequest;
import ru.inovus.mimimeter.controller.dto.VoteResponse;
import ru.inovus.mimimeter.model.dto.VotingPairDTO;
import ru.inovus.mimimeter.model.dto.VotingResultsDTO;
import ru.inovus.mimimeter.model.dto.VotingSessionDTO;
import ru.inovus.mimimeter.service.CandidatesService;
import ru.inovus.mimimeter.service.VotingService;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/voting")
public class VotingController {

    private final CandidatesService candidatesService;
    private final VotingService votingService;

    @GetMapping("/groups")
    public String candidatesGroups(Model model, Principal principal) {
        model.addAttribute("groups", candidatesService.getAllGroups());
        model.addAttribute("principal", principal);
        return "candidates_groups";
    }

    @GetMapping("/results/{groupId}")
    public String results(@PathVariable int groupId, Model model, Principal principal) {
        return candidatesService.getCandidatesGroup(groupId)
                .map(group -> {
                    VotingResultsDTO result = votingService.getVotingResults(groupId);
                    model.addAttribute("result", result);
                    model.addAttribute("group", group);
                    model.addAttribute("principal", principal);
                    return "results";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/start/{groupId}")
    public String startVoting(@PathVariable int groupId, Principal principal) {
        VotingSessionDTO session = votingService.getOrCreateSession(principal.getName(), groupId);
        if (session.getIsFinished() == 1) {
            return "redirect:/voting/results/" + groupId;
        }
        return votingService.getNextPair(session.getId())
                .map(pair -> "redirect:/voting/pair/" + pair.getId())
                .orElse("redirect:/voting/results/" + groupId);
    }

    @GetMapping("/pair/{id}")
    public String pair(@PathVariable int id, Model model, Principal principal) {
        VotingPairDTO pair = votingService.getPair(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("first", candidatesService.getCandidate(pair.getFirstCandidate()));
        model.addAttribute("second", candidatesService.getCandidate(pair.getSecondCandidate()));
        model.addAttribute("pairId", id);
        model.addAttribute("groupId", votingService.getGroupIdBySessionId(pair.getSessionId()));
        model.addAttribute("principal", principal);
        return "pair";
    }

    @PostMapping(
            value = "/vote",
            consumes = "application/json; charset=UTF-8",
            produces = "application/json; charset=UTF-8"
    )
    @ResponseBody
    public VoteResponse vote(@RequestBody VoteRequest voteRequest) {
        return votingService.getPair(voteRequest.getPairId())
                .filter(pair -> pair.getVerdict() == null)
                .map(pair -> {
                    votingService.vote(pair.getId(), voteRequest.getCandidateId());
                    return votingService.getNextPairByPreviousPair(voteRequest.getPairId())
                            .map(nextPair ->
                                    VoteResponse.builder()
                                            .nextPairId(nextPair.getId())
                                            .build()
                            ).orElseGet(() -> {
                                votingService.finishSession(pair.getSessionId());
                                return VoteResponse.builder().nextPairId(0).build();
                            });
                }).orElse(VoteResponse.builder().nextPairId(0).build());
    }

}
