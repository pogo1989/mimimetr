package ru.inovus.mimimeter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.inovus.mimimeter.model.entity.VotingSessionEntity;

import java.util.Optional;

@Repository
public interface VotingSessionRepository extends CrudRepository<VotingSessionEntity, Integer> {

    Optional<VotingSessionEntity> findByUsernameAndGroupId(String username, int groupId);

}
