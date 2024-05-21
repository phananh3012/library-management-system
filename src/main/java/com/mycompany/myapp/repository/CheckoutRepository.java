package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Checkout;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Checkout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long>, JpaSpecificationExecutor<Checkout> {}
