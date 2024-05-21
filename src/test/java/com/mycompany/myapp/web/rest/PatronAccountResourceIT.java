package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PatronAccountAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.PatronAccount;
import com.mycompany.myapp.repository.PatronAccountRepository;
import com.mycompany.myapp.service.dto.PatronAccountDTO;
import com.mycompany.myapp.service.mapper.PatronAccountMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PatronAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PatronAccountResourceIT {

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/patron-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PatronAccountRepository patronAccountRepository;

    @Autowired
    private PatronAccountMapper patronAccountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPatronAccountMockMvc;

    private PatronAccount patronAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PatronAccount createEntity(EntityManager em) {
        PatronAccount patronAccount = new PatronAccount()
            .cardNumber(DEFAULT_CARD_NUMBER)
            .firstName(DEFAULT_FIRST_NAME)
            .surname(DEFAULT_SURNAME)
            .email(DEFAULT_EMAIL)
            .status(DEFAULT_STATUS);
        return patronAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PatronAccount createUpdatedEntity(EntityManager em) {
        PatronAccount patronAccount = new PatronAccount()
            .cardNumber(UPDATED_CARD_NUMBER)
            .firstName(UPDATED_FIRST_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);
        return patronAccount;
    }

    @BeforeEach
    public void initTest() {
        patronAccount = createEntity(em);
    }

    @Test
    @Transactional
    void createPatronAccount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);
        var returnedPatronAccountDTO = om.readValue(
            restPatronAccountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patronAccountDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PatronAccountDTO.class
        );

        // Validate the PatronAccount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPatronAccount = patronAccountMapper.toEntity(returnedPatronAccountDTO);
        assertPatronAccountUpdatableFieldsEquals(returnedPatronAccount, getPersistedPatronAccount(returnedPatronAccount));
    }

    @Test
    @Transactional
    void createPatronAccountWithExistingId() throws Exception {
        // Create the PatronAccount with an existing ID
        patronAccount.setId(1L);
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatronAccountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patronAccountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPatronAccounts() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patronAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getPatronAccount() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get the patronAccount
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, patronAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(patronAccount.getId().intValue()))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getPatronAccountsByIdFiltering() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        Long id = patronAccount.getId();

        defaultPatronAccountFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPatronAccountFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPatronAccountFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByCardNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where cardNumber equals to
        defaultPatronAccountFiltering("cardNumber.equals=" + DEFAULT_CARD_NUMBER, "cardNumber.equals=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByCardNumberIsInShouldWork() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where cardNumber in
        defaultPatronAccountFiltering(
            "cardNumber.in=" + DEFAULT_CARD_NUMBER + "," + UPDATED_CARD_NUMBER,
            "cardNumber.in=" + UPDATED_CARD_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPatronAccountsByCardNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where cardNumber is not null
        defaultPatronAccountFiltering("cardNumber.specified=true", "cardNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllPatronAccountsByCardNumberContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where cardNumber contains
        defaultPatronAccountFiltering("cardNumber.contains=" + DEFAULT_CARD_NUMBER, "cardNumber.contains=" + UPDATED_CARD_NUMBER);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByCardNumberNotContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where cardNumber does not contain
        defaultPatronAccountFiltering(
            "cardNumber.doesNotContain=" + UPDATED_CARD_NUMBER,
            "cardNumber.doesNotContain=" + DEFAULT_CARD_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllPatronAccountsByFirstNameIsEqualToSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where firstName equals to
        defaultPatronAccountFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByFirstNameIsInShouldWork() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where firstName in
        defaultPatronAccountFiltering(
            "firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME,
            "firstName.in=" + UPDATED_FIRST_NAME
        );
    }

    @Test
    @Transactional
    void getAllPatronAccountsByFirstNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where firstName is not null
        defaultPatronAccountFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    @Transactional
    void getAllPatronAccountsByFirstNameContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where firstName contains
        defaultPatronAccountFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByFirstNameNotContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where firstName does not contain
        defaultPatronAccountFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsBySurnameIsEqualToSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where surname equals to
        defaultPatronAccountFiltering("surname.equals=" + DEFAULT_SURNAME, "surname.equals=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsBySurnameIsInShouldWork() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where surname in
        defaultPatronAccountFiltering("surname.in=" + DEFAULT_SURNAME + "," + UPDATED_SURNAME, "surname.in=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsBySurnameIsNullOrNotNull() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where surname is not null
        defaultPatronAccountFiltering("surname.specified=true", "surname.specified=false");
    }

    @Test
    @Transactional
    void getAllPatronAccountsBySurnameContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where surname contains
        defaultPatronAccountFiltering("surname.contains=" + DEFAULT_SURNAME, "surname.contains=" + UPDATED_SURNAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsBySurnameNotContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where surname does not contain
        defaultPatronAccountFiltering("surname.doesNotContain=" + UPDATED_SURNAME, "surname.doesNotContain=" + DEFAULT_SURNAME);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where email equals to
        defaultPatronAccountFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where email in
        defaultPatronAccountFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where email is not null
        defaultPatronAccountFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllPatronAccountsByEmailContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where email contains
        defaultPatronAccountFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where email does not contain
        defaultPatronAccountFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where status equals to
        defaultPatronAccountFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where status in
        defaultPatronAccountFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where status is not null
        defaultPatronAccountFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllPatronAccountsByStatusContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where status contains
        defaultPatronAccountFiltering("status.contains=" + DEFAULT_STATUS, "status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        // Get all the patronAccountList where status does not contain
        defaultPatronAccountFiltering("status.doesNotContain=" + UPDATED_STATUS, "status.doesNotContain=" + DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void getAllPatronAccountsByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            patronAccountRepository.saveAndFlush(patronAccount);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        patronAccount.addBook(book);
        patronAccountRepository.saveAndFlush(patronAccount);
        Long bookId = book.getId();
        // Get all the patronAccountList where book equals to bookId
        defaultPatronAccountShouldBeFound("bookId.equals=" + bookId);

        // Get all the patronAccountList where book equals to (bookId + 1)
        defaultPatronAccountShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultPatronAccountFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPatronAccountShouldBeFound(shouldBeFound);
        defaultPatronAccountShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPatronAccountShouldBeFound(String filter) throws Exception {
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patronAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));

        // Check, that the count call also returns 1
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPatronAccountShouldNotBeFound(String filter) throws Exception {
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPatronAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPatronAccount() throws Exception {
        // Get the patronAccount
        restPatronAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPatronAccount() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patronAccount
        PatronAccount updatedPatronAccount = patronAccountRepository.findById(patronAccount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPatronAccount are not directly saved in db
        em.detach(updatedPatronAccount);
        updatedPatronAccount
            .cardNumber(UPDATED_CARD_NUMBER)
            .firstName(UPDATED_FIRST_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(updatedPatronAccount);

        restPatronAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patronAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(patronAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPatronAccountToMatchAllProperties(updatedPatronAccount);
    }

    @Test
    @Transactional
    void putNonExistingPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, patronAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(patronAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(patronAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(patronAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePatronAccountWithPatch() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patronAccount using partial update
        PatronAccount partialUpdatedPatronAccount = new PatronAccount();
        partialUpdatedPatronAccount.setId(patronAccount.getId());

        partialUpdatedPatronAccount.firstName(UPDATED_FIRST_NAME).email(UPDATED_EMAIL);

        restPatronAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatronAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatronAccount))
            )
            .andExpect(status().isOk());

        // Validate the PatronAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatronAccountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPatronAccount, patronAccount),
            getPersistedPatronAccount(patronAccount)
        );
    }

    @Test
    @Transactional
    void fullUpdatePatronAccountWithPatch() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the patronAccount using partial update
        PatronAccount partialUpdatedPatronAccount = new PatronAccount();
        partialUpdatedPatronAccount.setId(patronAccount.getId());

        partialUpdatedPatronAccount
            .cardNumber(UPDATED_CARD_NUMBER)
            .firstName(UPDATED_FIRST_NAME)
            .surname(UPDATED_SURNAME)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);

        restPatronAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPatronAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPatronAccount))
            )
            .andExpect(status().isOk());

        // Validate the PatronAccount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPatronAccountUpdatableFieldsEquals(partialUpdatedPatronAccount, getPersistedPatronAccount(partialUpdatedPatronAccount));
    }

    @Test
    @Transactional
    void patchNonExistingPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, patronAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(patronAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(patronAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPatronAccount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        patronAccount.setId(longCount.incrementAndGet());

        // Create the PatronAccount
        PatronAccountDTO patronAccountDTO = patronAccountMapper.toDto(patronAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPatronAccountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(patronAccountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PatronAccount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePatronAccount() throws Exception {
        // Initialize the database
        patronAccountRepository.saveAndFlush(patronAccount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the patronAccount
        restPatronAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, patronAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return patronAccountRepository.count();
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

    protected PatronAccount getPersistedPatronAccount(PatronAccount patronAccount) {
        return patronAccountRepository.findById(patronAccount.getId()).orElseThrow();
    }

    protected void assertPersistedPatronAccountToMatchAllProperties(PatronAccount expectedPatronAccount) {
        assertPatronAccountAllPropertiesEquals(expectedPatronAccount, getPersistedPatronAccount(expectedPatronAccount));
    }

    protected void assertPersistedPatronAccountToMatchUpdatableProperties(PatronAccount expectedPatronAccount) {
        assertPatronAccountAllUpdatablePropertiesEquals(expectedPatronAccount, getPersistedPatronAccount(expectedPatronAccount));
    }
}
