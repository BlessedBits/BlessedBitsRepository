package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Achievement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    List<Achievement> findBySchoolId(int schoolId);
}
