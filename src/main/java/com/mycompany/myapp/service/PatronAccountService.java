package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.repository.PatronAccountRepository;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import com.mycompany.myapp.service.mapper.PatronAccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PatronAccount}.
 */
@Service
@Transactional
public class PatronAccountService {

    private final Logger log = LoggerFactory.getLogger(PatronAccountService.class);

    private final PatronAccountRepository patronAccountRepository;

    private final PatronAccountMapper patronAccountMapper;

    public PatronAccountService(PatronAccountRepository patronAccountRepository, PatronAccountMapper patronAccountMapper) {
        this.patronAccountRepository = patronAccountRepository;
        this.patronAccountMapper = patronAccountMapper;
    }

    /**
     * Save a patronAccount.
     *
     * @param patronAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public PatronAccountDTO save(PatronAccountDTO patronAccountDTO) {
        log.debug("Request to save PatronAccount : {}", patronAccountDTO);
        PatronAccount patronAccount = patronAccountMapper.toEntity(patronAccountDTO);
        patronAccount = patronAccountRepository.save(patronAccount);
        return patronAccountMapper.toDto(patronAccount);
    }

    /**
     * Update a patronAccount.
     *
     * @param patronAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public PatronAccountDTO update(PatronAccountDTO patronAccountDTO) {
        log.debug("Request to update PatronAccount : {}", patronAccountDTO);
        PatronAccount patronAccount = patronAccountMapper.toEntity(patronAccountDTO);
        patronAccount = patronAccountRepository.save(patronAccount);
        return patronAccountMapper.toDto(patronAccount);
    }

    /**
     * Partially update a patronAccount.
     *
     * @param patronAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PatronAccountDTO> partialUpdate(PatronAccountDTO patronAccountDTO) {
        log.debug("Request to partially update PatronAccount : {}", patronAccountDTO);

        return patronAccountRepository
            .findById(patronAccountDTO.getId())
            .map(existingPatronAccount -> {
                patronAccountMapper.partialUpdate(existingPatronAccount, patronAccountDTO);

                return existingPatronAccount;
            })
            .map(patronAccountRepository::save)
            .map(patronAccountMapper::toDto);
    }

    /**
     * Get one patronAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PatronAccountDTO> findOne(Long id) {
        log.debug("Request to get PatronAccount : {}", id);
        return patronAccountRepository.findById(id).map(patronAccountMapper::toDto);
    }

    /**
     * Delete the patronAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete PatronAccount : {}", id);
        patronAccountRepository.deleteById(id);
    }
}
