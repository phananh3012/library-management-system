package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CheckoutAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Checkout;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.repository.CheckoutRepository;
import com.mycompany.myapp.service.dto.CheckoutDTO;
import com.mycompany.myapp.service.mapper.CheckoutMapper;
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
 * Integration tests for the {@link CheckoutResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CheckoutResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_IS_RETURNED = false;
    private static final Boolean UPDATED_IS_RETURNED = true;

    private static final String ENTITY_API_URL = "/api/checkouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private CheckoutMapper checkoutMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCheckoutMockMvc;

    private Checkout checkout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createEntity(EntityManager em) {
        Checkout checkout = new Checkout().startTime(DEFAULT_START_TIME).endTime(DEFAULT_END_TIME).isReturned(DEFAULT_IS_RETURNED);
        return checkout;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createUpdatedEntity(EntityManager em) {
        Checkout checkout = new Checkout().startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);
        return checkout;
    }

    @BeforeEach
    public void initTest() {
        checkout = createEntity(em);
    }

    @Test
    @Transactional
    void createCheckout() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);
        var returnedCheckoutDTO = om.readValue(
            restCheckoutMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkoutDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CheckoutDTO.class
        );

        // Validate the Checkout in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCheckout = checkoutMapper.toEntity(returnedCheckoutDTO);
        assertCheckoutUpdatableFieldsEquals(returnedCheckout, getPersistedCheckout(returnedCheckout));
    }

    @Test
    @Transactional
    void createCheckoutWithExistingId() throws Exception {
        // Create the Checkout with an existing ID
        checkout.setId(1L);
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCheckoutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkoutDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCheckouts() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkout.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].isReturned").value(hasItem(DEFAULT_IS_RETURNED.booleanValue())));
    }

    @Test
    @Transactional
    void getCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get the checkout
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL_ID, checkout.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(checkout.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.isReturned").value(DEFAULT_IS_RETURNED.booleanValue()));
    }

    @Test
    @Transactional
    void getCheckoutsByIdFiltering() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        Long id = checkout.getId();

        defaultCheckoutFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCheckoutFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCheckoutFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCheckoutsByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where startTime equals to
        defaultCheckoutFiltering("startTime.equals=" + DEFAULT_START_TIME, "startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCheckoutsByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where startTime in
        defaultCheckoutFiltering("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME, "startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    void getAllCheckoutsByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where startTime is not null
        defaultCheckoutFiltering("startTime.specified=true", "startTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCheckoutsByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where endTime equals to
        defaultCheckoutFiltering("endTime.equals=" + DEFAULT_END_TIME, "endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCheckoutsByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where endTime in
        defaultCheckoutFiltering("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME, "endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    void getAllCheckoutsByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where endTime is not null
        defaultCheckoutFiltering("endTime.specified=true", "endTime.specified=false");
    }

    @Test
    @Transactional
    void getAllCheckoutsByIsReturnedIsEqualToSomething() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where isReturned equals to
        defaultCheckoutFiltering("isReturned.equals=" + DEFAULT_IS_RETURNED, "isReturned.equals=" + UPDATED_IS_RETURNED);
    }

    @Test
    @Transactional
    void getAllCheckoutsByIsReturnedIsInShouldWork() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where isReturned in
        defaultCheckoutFiltering(
            "isReturned.in=" + DEFAULT_IS_RETURNED + "," + UPDATED_IS_RETURNED,
            "isReturned.in=" + UPDATED_IS_RETURNED
        );
    }

    @Test
    @Transactional
    void getAllCheckoutsByIsReturnedIsNullOrNotNull() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        // Get all the checkoutList where isReturned is not null
        defaultCheckoutFiltering("isReturned.specified=true", "isReturned.specified=false");
    }

    @Test
    @Transactional
    void getAllCheckoutsByBookCopyIsEqualToSomething() throws Exception {
        BookCopy bookCopy;
        if (TestUtil.findAll(em, BookCopy.class).isEmpty()) {
            checkoutRepository.saveAndFlush(checkout);
            bookCopy = BookCopyResourceIT.createEntity(em);
        } else {
            bookCopy = TestUtil.findAll(em, BookCopy.class).get(0);
        }
        em.persist(bookCopy);
        em.flush();
        checkout.setBookCopy(bookCopy);
        checkoutRepository.saveAndFlush(checkout);
        Long bookCopyId = bookCopy.getId();
        // Get all the checkoutList where bookCopy equals to bookCopyId
        defaultCheckoutShouldBeFound("bookCopyId.equals=" + bookCopyId);

        // Get all the checkoutList where bookCopy equals to (bookCopyId + 1)
        defaultCheckoutShouldNotBeFound("bookCopyId.equals=" + (bookCopyId + 1));
    }

    @Test
    @Transactional
    void getAllCheckoutsByPatronAccountIsEqualToSomething() throws Exception {
        PatronAccount patronAccount;
        if (TestUtil.findAll(em, PatronAccount.class).isEmpty()) {
            checkoutRepository.saveAndFlush(checkout);
            patronAccount = PatronAccountResourceIT.createEntity(em);
        } else {
            patronAccount = TestUtil.findAll(em, PatronAccount.class).get(0);
        }
        em.persist(patronAccount);
        em.flush();
        checkout.setPatronAccount(patronAccount);
        checkoutRepository.saveAndFlush(checkout);
        Long patronAccountId = patronAccount.getId();
        // Get all the checkoutList where patronAccount equals to patronAccountId
        defaultCheckoutShouldBeFound("patronAccountId.equals=" + patronAccountId);

        // Get all the checkoutList where patronAccount equals to (patronAccountId + 1)
        defaultCheckoutShouldNotBeFound("patronAccountId.equals=" + (patronAccountId + 1));
    }

    private void defaultCheckoutFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCheckoutShouldBeFound(shouldBeFound);
        defaultCheckoutShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCheckoutShouldBeFound(String filter) throws Exception {
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(checkout.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].isReturned").value(hasItem(DEFAULT_IS_RETURNED.booleanValue())));

        // Check, that the count call also returns 1
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCheckoutShouldNotBeFound(String filter) throws Exception {
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCheckoutMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCheckout() throws Exception {
        // Get the checkout
        restCheckoutMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkout
        Checkout updatedCheckout = checkoutRepository.findById(checkout.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCheckout are not directly saved in db
        em.detach(updatedCheckout);
        updatedCheckout.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(updatedCheckout);

        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkoutDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkoutDTO))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCheckoutToMatchAllProperties(updatedCheckout);
    }

    @Test
    @Transactional
    void putNonExistingCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, checkoutDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkoutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(checkoutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(checkoutDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.startTime(UPDATED_START_TIME);

        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckout))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckoutUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCheckout, checkout), getPersistedCheckout(checkout));
    }

    @Test
    @Transactional
    void fullUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.startTime(UPDATED_START_TIME).endTime(UPDATED_END_TIME).isReturned(UPDATED_IS_RETURNED);

        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCheckout))
            )
            .andExpect(status().isOk());

        // Validate the Checkout in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCheckoutUpdatableFieldsEquals(partialUpdatedCheckout, getPersistedCheckout(partialUpdatedCheckout));
    }

    @Test
    @Transactional
    void patchNonExistingCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, checkoutDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkoutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(checkoutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCheckout() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        checkout.setId(longCount.incrementAndGet());

        // Create the Checkout
        CheckoutDTO checkoutDTO = checkoutMapper.toDto(checkout);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCheckoutMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(checkoutDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Checkout in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.saveAndFlush(checkout);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the checkout
        restCheckoutMockMvc
            .perform(delete(ENTITY_API_URL_ID, checkout.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return checkoutRepository.count();
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

    protected Checkout getPersistedCheckout(Checkout checkout) {
        return checkoutRepository.findById(checkout.getId()).orElseThrow();
    }

    protected void assertPersistedCheckoutToMatchAllProperties(Checkout expectedCheckout) {
        assertCheckoutAllPropertiesEquals(expectedCheckout, getPersistedCheckout(expectedCheckout));
    }

    protected void assertPersistedCheckoutToMatchUpdatableProperties(Checkout expectedCheckout) {
        assertCheckoutAllUpdatablePropertiesEquals(expectedCheckout, getPersistedCheckout(expectedCheckout));
    }
}
