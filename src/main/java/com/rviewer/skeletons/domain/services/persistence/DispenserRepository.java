package com.rviewer.skeletons.domain.services.persistence;

import com.rviewer.skeletons.domain.entities.Dispenser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispenserRepository extends JpaRepository<Dispenser, Long> {}
