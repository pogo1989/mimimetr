package ru.inovus.mimimeter.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.inovus.mimimeter.model.entity.CandidatesGroupEntity;

import java.util.List;

@Repository
public interface CandidatesGroupRepository extends CrudRepository<CandidatesGroupEntity, Integer> {

    List<CandidatesGroupEntity> findAll();

}
