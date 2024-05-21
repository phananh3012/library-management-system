package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PatronAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PatronAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PatronAccountRepository extends JpaRepository<PatronAccount, Long>, JpaSpecificationExecutor<PatronAccount> {}
