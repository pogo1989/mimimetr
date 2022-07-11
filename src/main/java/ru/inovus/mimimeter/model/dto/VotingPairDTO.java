package ru.inovus.mimimeter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingPairDTO {

    private Integer id;

    private int sessionId;

    private int firstCandidate;

    private int secondCandidate;

    private Integer verdict;

}
