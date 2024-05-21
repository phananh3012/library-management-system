package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BookCopy;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BookCopy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long>, JpaSpecificationExecutor<BookCopy> {}
