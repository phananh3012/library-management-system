package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Hold;
import com.mycompany.myapp.repository.HoldRepository;
import com.mycompany.myapp.service.dto.HoldDTO;
import com.mycompany.myapp.service.mapper.HoldMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Hold}.
 */
@Service
@Transactional
public class HoldService {

    private final Logger log = LoggerFactory.getLogger(HoldService.class);

    private final HoldRepository holdRepository;

    private final HoldMapper holdMapper;

    public HoldService(HoldRepository holdRepository, HoldMapper holdMapper) {
        this.holdRepository = holdRepository;
        this.holdMapper = holdMapper;
    }

    /**
     * Save a hold.
     *
     * @param holdDTO the entity to save.
     * @return the persisted entity.
     */
    public HoldDTO save(HoldDTO holdDTO) {
        log.debug("Request to save Hold : {}", holdDTO);
        Hold hold = holdMapper.toEntity(holdDTO);
        hold = holdRepository.save(hold);
        return holdMapper.toDto(hold);
    }

    /**
     * Update a hold.
     *
     * @param holdDTO the entity to save.
     * @return the persisted entity.
     */
    public HoldDTO update(HoldDTO holdDTO) {
        log.debug("Request to update Hold : {}", holdDTO);
        Hold hold = holdMapper.toEntity(holdDTO);
        hold = holdRepository.save(hold);
        return holdMapper.toDto(hold);
    }

    /**
     * Partially update a hold.
     *
     * @param holdDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HoldDTO> partialUpdate(HoldDTO holdDTO) {
        log.debug("Request to partially update Hold : {}", holdDTO);

        return holdRepository
            .findById(holdDTO.getId())
            .map(existingHold -> {
                holdMapper.partialUpdate(existingHold, holdDTO);

                return existingHold;
            })
            .map(holdRepository::save)
            .map(holdMapper::toDto);
    }

    /**
     * Get one hold by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HoldDTO> findOne(Long id) {
        log.debug("Request to get Hold : {}", id);
        return holdRepository.findById(id).map(holdMapper::toDto);
    }

    /**
     * Delete the hold by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Hold : {}", id);
        holdRepository.deleteById(id);
    }
}
