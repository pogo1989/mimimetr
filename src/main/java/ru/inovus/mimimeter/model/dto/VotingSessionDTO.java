package ru.inovus.mimimeter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotingSessionDTO {

    private Integer id;

    private String username;

    private int groupId;

    private int isFinished;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
