package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.HoldAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Hold;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.repository.HoldRepository;
import com.mycompany.myapp.service.dto.HoldDTO;
import com.mycompany.myapp.service.mapper.HoldMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link HoldResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HoldResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/holds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HoldRepository holdRepository;

    @Autowired
    private HoldMapper holdMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHoldMockMvc;

    private Hold hold;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createEntity(EntityManager em) {
        Hold hold = new Hold().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME);
        return hold;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hold createUpdatedEntity(EntityManager em) {
        Hold hold = new Hold().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        return hold;
    }

    @BeforeEach
    public void initTest() {
        hold = createEntity(em);
    }

    @Test
    @Transactional
    void createHold() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);
        var returnedHoldDTO = om.readValue(
            restHoldMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holdDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HoldDTO.class
        );

        // Validate the Hold in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHold = holdMapper.toEntity(returnedHoldDTO);
        assertHoldUpdatableFieldsEquals(returnedHold, getPersistedHold(returnedHold));
    }

    @Test
    @Transactional
    void createHoldWithExistingId() throws Exception {
        // Create the Hold with an existing ID
        hold.setId(1L);
        HoldDTO holdDTO = holdMapper.toDto(hold);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHoldMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holdDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHolds() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hold.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));
    }

    @Test
    @Transactional
    void getHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get the hold
        restHoldMockMvc
            .perform(get(ENTITY_API_URL_ID, hold.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hold.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()));
    }

    @Test
    @Transactional
    void getHoldsByIdFiltering() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        Long id = hold.getId();

        defaultHoldFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHoldFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHoldFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHoldsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where startTime equals to
        defaultHoldFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllHoldsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where startTime in
        defaultHoldFiltering("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME, "startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllHoldsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where startTime is not null
        defaultHoldFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllHoldsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where endTime equals to
        defaultHoldFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllHoldsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where endTime in
        defaultHoldFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllHoldsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        // Get all the holdList where endTime is not null
        defaultHoldFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllHoldsByBookCopyIsEqualToSomething() throws Exception {
        BookCopy bookCopy;
        if (TestUtil.findAll(em, BookCopy.class).isEmpty()) {
            holdRepository.saveAndFlush(hold);
            bookCopy = BookCopyResourceIT.createEntity(em);
        } else {
            bookCopy = TestUtil.findAll(em, BookCopy.class).get(0);
        }
        em.persist(bookCopy);
        em.flush();
        hold.setBookCopy(bookCopy);
        holdRepository.saveAndFlush(hold);
        Long bookCopyId = bookCopy.getId();
        // Get all the holdList where bookCopy equals to bookCopyId
        defaultHoldShouldBeFound("bookCopyId.equals=" + bookCopyId);

        // Get all the holdList where bookCopy equals to (bookCopyId + 1)
        defaultHoldShouldNotBeFound("bookCopyId.equals=" + (bookCopyId + 1));
    }

    @Test
    @Transactional
    void getAllHoldsByPatronAccountIsEqualToSomething() throws Exception {
        PatronAccount patronAccount;
        if (TestUtil.findAll(em, PatronAccount.class).isEmpty()) {
            holdRepository.saveAndFlush(hold);
            patronAccount = PatronAccountResourceIT.createEntity(em);
        } else {
            patronAccount = TestUtil.findAll(em, PatronAccount.class).get(0);
        }
        em.persist(patronAccount);
        em.flush();
        hold.setPatronAccount(patronAccount);
        holdRepository.saveAndFlush(hold);
        Long patronAccountId = patronAccount.getId();
        // Get all the holdList where patronAccount equals to patronAccountId
        defaultHoldShouldBeFound("patronAccountId.equals=" + patronAccountId);

        // Get all the holdList where patronAccount equals to (patronAccountId + 1)
        defaultHoldShouldNotBeFound("patronAccountId.equals=" + (patronAccountId + 1));
    }

    private void defaultHoldFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHoldShouldBeFound(shouldBeFound);
        defaultHoldShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHoldShouldBeFound(String filter) throws Exception {
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hold.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())));

        // Check, that the count call also returns 1
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHoldShouldNotBeFound(String filter) throws Exception {
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHoldMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHold() throws Exception {
        // Get the hold
        restHoldMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold
        Hold updatedHold = holdRepository.findById(hold.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHold are not directly saved in db
        em.detach(updatedHold);
        updatedHold.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);
        HoldDTO holdDTO = holdMapper.toDto(updatedHold);

        restHoldMockMvc
            .perform(put(ENTITY_API_URL_ID, holdDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holdDTO)))
            .andExpect(status().isOk());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHoldToMatchAllProperties(updatedHold);
    }

    @Test
    @Transactional
    void putNonExistingHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(put(ENTITY_API_URL_ID, holdDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holdDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(holdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(holdDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold.endTime(UPDATED_END_TIME);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHold, hold), getPersistedHold(hold));
    }

    @Test
    @Transactional
    void fullUpdateHoldWithPatch() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the hold using partial update
        Hold partialUpdatedHold = new Hold();
        partialUpdatedHold.setId(hold.getId());

        partialUpdatedHold.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME);

        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHold.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHold))
            )
            .andExpect(status().isOk());

        // Validate the Hold in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHoldUpdatableFieldsEquals(partialUpdatedHold, getPersistedHold(partialUpdatedHold));
    }

    @Test
    @Transactional
    void patchNonExistingHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, holdDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(holdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(holdDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHold() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        hold.setId(longCount.incrementAndGet());

        // Create the Hold
        HoldDTO holdDTO = holdMapper.toDto(hold);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHoldMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(holdDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hold in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHold() throws Exception {
        // Initialize the database
        holdRepository.saveAndFlush(hold);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the hold
        restHoldMockMvc
            .perform(delete(ENTITY_API_URL_ID, hold.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return holdRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Hold getPersistedHold(Hold hold) {
        return holdRepository.findById(hold.getId()).orElseThrow();
    }

    protected void assertPersistedHoldToMatchAllProperties(Hold expectedHold) {
        assertHoldAllPropertiesEquals(expectedHold, getPersistedHold(expectedHold));
    }

    protected void assertPersistedHoldToMatchUpdatableProperties(Hold expectedHold) {
        assertHoldAllUpdatablePropertiesEquals(expectedHold, getPersistedHold(expectedHold));
    }
}
