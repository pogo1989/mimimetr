package ru.inovus.mimimeter.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.inovus.mimimeter.model.dto.CandidateDTO;
import ru.inovus.mimimeter.model.dto.CandidatesGroupDTO;
import ru.inovus.mimimeter.model.entity.CandidatesGroupEntity;
import ru.inovus.mimimeter.repository.CandidateRepository;
import ru.inovus.mimimeter.repository.CandidatesGroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidatesService {

    private final CandidatesGroupRepository candidatesGroupRepository;

    private final CandidateRepository candidateRepository;

    private final ModelMapper modelMapper;

    public List<CandidatesGroupDTO> getAllGroups() {
        List<CandidatesGroupEntity> groups = candidatesGroupRepository.findAll();
        return groups.stream()
                .map(group -> modelMapper.map(group, CandidatesGroupDTO.class))
                .collect(Collectors.toList());
    }

    public CandidateDTO getCandidate(int id) {
        return candidateRepository.findById(id).map(group -> modelMapper.map(group, CandidateDTO.class)).get();
    }

    public Optional<CandidatesGroupDTO> getCandidatesGroup(int groupId) {
        return candidatesGroupRepository.findById(groupId)
                .map(group -> modelMapper.map(group, CandidatesGroupDTO.class));
    }
}
