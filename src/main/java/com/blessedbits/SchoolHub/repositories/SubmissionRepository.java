package com.blessedbits.SchoolHub.repositories;

import com.blessedbits.SchoolHub.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

}
