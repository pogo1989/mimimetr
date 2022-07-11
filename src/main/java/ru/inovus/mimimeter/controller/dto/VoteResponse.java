package ru.inovus.mimimeter.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponse {

    private int nextPairId;

}
