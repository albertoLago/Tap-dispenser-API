package com.rviewer.skeletons.domain.services.persistence;

import com.rviewer.skeletons.domain.entities.DispenserTapUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispenserTapUsageRepository extends JpaRepository<DispenserTapUsage, Long> {

    List<DispenserTapUsage> findByDispenserID(long dispenserID);
    Optional<DispenserTapUsage> findByDispenserIDAndEndedAtIsNull(long dispenserId);
}
