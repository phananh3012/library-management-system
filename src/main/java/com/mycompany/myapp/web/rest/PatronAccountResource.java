package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PatronAccountRepository;
import com.mycompany.myapp.service.PatronAccountService;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.PatronAccount}.
 */
@RestController
@RequestMapping("/api/patron-accounts")
public class PatronAccountResource {

    private final Logger log = LoggerFactory.getLogger(PatronAccountResource.class);

    private static final String ENTITY_NAME = "patronAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PatronAccountService patronAccountService;

    private final PatronAccountRepository patronAccountRepository;

    public PatronAccountResource(PatronAccountService patronAccountService, PatronAccountRepository patronAccountRepository) {
        this.patronAccountService = patronAccountService;
        this.patronAccountRepository = patronAccountRepository;
    }

    /**
     * {@code POST  /patron-accounts} : Create a new patronAccount.
     *
     * @param patronAccountDTO the patronAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new patronAccountDTO, or with status {@code 400 (Bad Request)} if the patronAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PatronAccountDTO> createPatronAccount(@RequestBody PatronAccountDTO patronAccountDTO) throws URISyntaxException {
        log.debug("REST request to save PatronAccount : {}", patronAccountDTO);
        if (patronAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new patronAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        patronAccountDTO = patronAccountService.save(patronAccountDTO);
        return ResponseEntity.created(new URI("/api/patron-accounts/" + patronAccountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, patronAccountDTO.getId().toString()))
            .body(patronAccountDTO);
    }

    /**
     * {@code PUT  /patron-accounts/:id} : Updates an existing patronAccount.
     *
     * @param id the id of the patronAccountDTO to save.
     * @param patronAccountDTO the patronAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated patronAccountDTO,
     * or with status {@code 400 (Bad Request)} if the patronAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the patronAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatronAccountDTO> updatePatronAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PatronAccountDTO patronAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PatronAccount : {}, {}", id, patronAccountDTO);
        if (patronAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, patronAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patronAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        patronAccountDTO = patronAccountService.update(patronAccountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, patronAccountDTO.getId().toString()))
            .body(patronAccountDTO);
    }

    /**
     * {@code PATCH  /patron-accounts/:id} : Partial updates given fields of an existing patronAccount, field will ignore if it is null
     *
     * @param id the id of the patronAccountDTO to save.
     * @param patronAccountDTO the patronAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated patronAccountDTO,
     * or with status {@code 400 (Bad Request)} if the patronAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the patronAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the patronAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PatronAccountDTO> partialUpdatePatronAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PatronAccountDTO patronAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PatronAccount partially : {}, {}", id, patronAccountDTO);
        if (patronAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, patronAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!patronAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PatronAccountDTO> result = patronAccountService.partialUpdate(patronAccountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, patronAccountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /patron-accounts/:id} : get the "id" patronAccount.
     *
     * @param id the id of the patronAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the patronAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatronAccountDTO> getPatronAccount(@PathVariable("id") Long id) {
        log.debug("REST request to get PatronAccount : {}", id);
        Optional<PatronAccountDTO> patronAccountDTO = patronAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(patronAccountDTO);
    }

    /**
     * {@code DELETE  /patron-accounts/:id} : delete the "id" patronAccount.
     *
     * @param id the id of the patronAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatronAccount(@PathVariable("id") Long id) {
        log.debug("REST request to delete PatronAccount : {}", id);
        patronAccountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
