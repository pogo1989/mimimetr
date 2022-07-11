package ru.inovus.mimimeter.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.inovus.mimimeter.model.dto.*;
import ru.inovus.mimimeter.model.entity.CandidateEntity;
import ru.inovus.mimimeter.model.entity.VotingPairEntity;
import ru.inovus.mimimeter.model.entity.VotingSessionEntity;
import ru.inovus.mimimeter.repository.CandidateRepository;
import ru.inovus.mimimeter.repository.VotingPairRepository;
import ru.inovus.mimimeter.repository.VotingSessionRepository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotingService {

    private final CandidateRepository candidateRepository;

    private final VotingSessionRepository votingSessionRepository;

    private final VotingPairRepository votingPairRepository;

    private final CandidatesService candidatesService;

    private final ModelMapper modelMapper;

    private final Clock clock;

    @Transactional
    public VotingSessionDTO getOrCreateSession(String user, int groupId) {
        return votingSessionRepository.findByUsernameAndGroupId(user, groupId)
                .map(session -> modelMapper.map(session, VotingSessionDTO.class))
                .orElseGet(() -> {
                    VotingSessionEntity session = votingSessionRepository.save(VotingSessionEntity.builder()
                            .username(user)
                            .groupId(groupId)
                            .createdAt(Timestamp.from(Instant.now(clock)))
                            .updatedAt(Timestamp.from(Instant.now(clock)))
                            .build());
                    List<CandidateEntity> candidates = candidateRepository.findAllByGroupId(groupId);
                    List<VotingPairEntity> pairs = new ArrayList<>();
                    for (int i = 0; i < candidates.size(); i++) {
                        for (int j = i + 1; j < candidates.size(); j++) {
                            int left = i;
                            int right = j;
                            if ((int) (Math.random() * 2) == 0) {
                                left = j;
                                right = i;
                            }
                            pairs.add(VotingPairEntity.builder()
                                    .sessionId(session.getId())
                                    .firstCandidate(candidates.get(left).getId())
                                    .secondCandidate(candidates.get(right).getId())
                                    .build());
                        }
                    }
                    Collections.shuffle(pairs);
                    votingPairRepository.saveAll(pairs);
                    return modelMapper.map(session, VotingSessionDTO.class);
                });
    }

    @Transactional(readOnly = true)
    public Optional<VotingPairDTO> getNextPair(Integer sessionId) {
        return votingPairRepository.findFirstBySessionIdAndVerdict(sessionId, null)
                .map(entity -> modelMapper.map(entity, VotingPairDTO.class));
    }

    @Transactional(readOnly = true)
    public Optional<VotingPairDTO> getPair(int id) {
        return votingPairRepository.findById(id)
                .map(entity -> modelMapper.map(entity, VotingPairDTO.class));
    }

    @Transactional
    public void vote(int pairId, int candidateId) {
        votingPairRepository.findById(pairId)
                .filter(pair -> pair.getVerdict() == null)
                .ifPresent(pair -> {
                    if (pair.getFirstCandidate() == candidateId || pair.getSecondCandidate() == candidateId) {
                        pair.setVerdict(candidateId);
                        votingPairRepository.save(pair);
                    }
                    votingSessionRepository.findById(pair.getSessionId()).ifPresent(session -> {
                        session.setUpdatedAt(Timestamp.from(Instant.now(clock)));
                        votingSessionRepository.save(session);
                    });
                });
    }

    @Transactional(readOnly = true)
    public Optional<VotingPairDTO> getNextPairByPreviousPair(int pairId) {
        return votingPairRepository.findById(pairId)
                .flatMap(pair -> getNextPair(pair.getSessionId()));
    }

    public VotingResultsDTO getVotingResults(int groupId) {
        List<CandidateEntity> candidates = candidateRepository.findFirst10ByGroupIdOrderByVotesCountDesc(groupId);
        return VotingResultsDTO.builder()
                .candidates(candidates.stream()
                        .map(candidate -> modelMapper.map(candidate, CandidateDTO.class))
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional(readOnly = true)
    public int getGroupIdBySessionId(int sessionId) {
        return votingSessionRepository.findById(sessionId).map(VotingSessionEntity::getGroupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
    }

    @Transactional
    public void finishSession(int sessionId) {
        int groupId = getGroupIdBySessionId(sessionId);
        Map<Integer, CandidateEntity> candidates = candidateRepository.findAllByGroupId(groupId)
                .stream().collect(Collectors.toMap(CandidateEntity::getId, Function.identity()));
        List<VotingPairEntity> pairs = votingPairRepository.findAllBySessionId(sessionId);
        for (VotingPairEntity pair : pairs) {
            CandidateEntity candidate = candidates.get(pair.getVerdict());
            candidate.setVotesCount(candidate.getVotesCount() + 1);
        }
        candidateRepository.saveAll(candidates.values());
        votingPairRepository.deleteAll(pairs);
        votingSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setIsFinished(1);
            votingSessionRepository.save(session);
        });
    }
}
