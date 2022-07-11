package ru.inovus.mimimeter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.inovus.mimimeter.model.entity.CandidateEntity;

import java.util.List;

@Repository
public interface CandidateRepository extends CrudRepository<CandidateEntity, Integer> {
    List<CandidateEntity> findAllByGroupId(int groupId);

    List<CandidateEntity> findFirst10ByGroupIdOrderByVotesCountDesc(int groupId);

}
