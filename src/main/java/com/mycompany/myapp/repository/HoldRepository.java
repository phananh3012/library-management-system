package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Hold;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hold entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HoldRepository extends JpaRepository<Hold, Long>, JpaSpecificationExecutor<Hold> {}
