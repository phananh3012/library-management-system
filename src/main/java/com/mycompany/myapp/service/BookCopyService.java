package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.repository.BookCopyRepository;
import com.mycompany.myapp.service.dto.BookCopyDTO;
import com.mycompany.myapp.service.mapper.BookCopyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BookCopy}.
 */
@Service
@Transactional
public class BookCopyService {

    private final Logger log = LoggerFactory.getLogger(BookCopyService.class);

    private final BookCopyRepository bookCopyRepository;

    private final BookCopyMapper bookCopyMapper;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookCopyMapper bookCopyMapper) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookCopyMapper = bookCopyMapper;
    }

    /**
     * Save a bookCopy.
     *
     * @param bookCopyDTO the entity to save.
     * @return the persisted entity.
     */
    public BookCopyDTO save(BookCopyDTO bookCopyDTO) {
        log.debug("Request to save BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    /**
     * Update a bookCopy.
     *
     * @param bookCopyDTO the entity to save.
     * @return the persisted entity.
     */
    public BookCopyDTO update(BookCopyDTO bookCopyDTO) {
        log.debug("Request to update BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    /**
     * Partially update a bookCopy.
     *
     * @param bookCopyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BookCopyDTO> partialUpdate(BookCopyDTO bookCopyDTO) {
        log.debug("Request to partially update BookCopy : {}", bookCopyDTO);

        return bookCopyRepository
            .findById(bookCopyDTO.getId())
            .map(existingBookCopy -> {
                bookCopyMapper.partialUpdate(existingBookCopy, bookCopyDTO);

                return existingBookCopy;
            })
            .map(bookCopyRepository::save)
            .map(bookCopyMapper::toDto);
    }

    /**
     * Get one bookCopy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BookCopyDTO> findOne(Long id) {
        log.debug("Request to get BookCopy : {}", id);
        return bookCopyRepository.findById(id).map(bookCopyMapper::toDto);
    }

    /**
     * Delete the bookCopy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BookCopy : {}", id);
        bookCopyRepository.deleteById(id);
    }
}
