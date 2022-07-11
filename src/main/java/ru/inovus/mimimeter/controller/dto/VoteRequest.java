package ru.inovus.mimimeter.controller.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private int pairId;
    private int candidateId;
}
