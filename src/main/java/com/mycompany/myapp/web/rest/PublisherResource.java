package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PublisherRepository;
import com.mycompany.myapp.service.PublisherService;
import com.mycompany.myapp.service.dto.PublisherDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Publisher}.
 */
@RestController
@RequestMapping("/api/publishers")
public class PublisherResource {

    private final Logger log = LoggerFactory.getLogger(PublisherResource.class);

    private static final String ENTITY_NAME = "publisher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublisherService publisherService;

    private final PublisherRepository publisherRepository;

    public PublisherResource(PublisherService publisherService, PublisherRepository publisherRepository) {
        this.publisherService = publisherService;
        this.publisherRepository = publisherRepository;
    }

    /**
     * {@code POST  /publishers} : Create a new publisher.
     *
     * @param publisherDTO the publisherDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new publisherDTO, or with status {@code 400 (Bad Request)} if the publisher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PublisherDTO> createPublisher(@RequestBody PublisherDTO publisherDTO) throws URISyntaxException {
        log.debug("REST request to save Publisher : {}", publisherDTO);
        if (publisherDTO.getId() != null) {
            throw new BadRequestAlertException("A new publisher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        publisherDTO = publisherService.save(publisherDTO);
        return ResponseEntity.created(new URI("/api/publishers/" + publisherDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString()))
            .body(publisherDTO);
    }

    /**
     * {@code PUT  /publishers/:id} : Updates an existing publisher.
     *
     * @param id the id of the publisherDTO to save.
     * @param publisherDTO the publisherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publisherDTO,
     * or with status {@code 400 (Bad Request)} if the publisherDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publisherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO> updatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Publisher : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publisherDTO = publisherService.update(publisherDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString()))
            .body(publisherDTO);
    }

    /**
     * {@code PATCH  /publishers/:id} : Partial updates given fields of an existing publisher, field will ignore if it is null
     *
     * @param id the id of the publisherDTO to save.
     * @param publisherDTO the publisherDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publisherDTO,
     * or with status {@code 400 (Bad Request)} if the publisherDTO is not valid,
     * or with status {@code 404 (Not Found)} if the publisherDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the publisherDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublisherDTO> partialUpdatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Publisher partially : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublisherDTO> result = publisherService.partialUpdate(publisherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /publishers/:id} : get the "id" publisher.
     *
     * @param id the id of the publisherDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publisherDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO> getPublisher(@PathVariable("id") Long id) {
        log.debug("REST request to get Publisher : {}", id);
        Optional<PublisherDTO> publisherDTO = publisherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publisherDTO);
    }

    /**
     * {@code DELETE  /publishers/:id} : delete the "id" publisher.
     *
     * @param id the id of the publisherDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable("id") Long id) {
        log.debug("REST request to delete Publisher : {}", id);
        publisherService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
