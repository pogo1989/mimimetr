package ru.inovus.mimimeter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {

    private Integer id;

    private int groupId;

    private String name;

    private String imgUrl;

    private int votesCount;

}
