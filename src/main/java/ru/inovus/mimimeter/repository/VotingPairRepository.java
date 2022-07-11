package ru.inovus.mimimeter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.inovus.mimimeter.model.entity.VotingPairEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface VotingPairRepository extends CrudRepository<VotingPairEntity, Integer> {

    Optional<VotingPairEntity> findFirstBySessionIdAndVerdict(int sessionId, Integer verdict);

    List<VotingPairEntity> findAllBySessionId(int sessionId);
}
