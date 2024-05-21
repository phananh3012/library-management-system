package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BookCopyAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.domain.BookCopy;
import com.mycompany.myapp.domain.Publisher;
import com.mycompany.myapp.repository.BookCopyRepository;
import com.mycompany.myapp.service.dto.BookCopyDTO;
import com.mycompany.myapp.service.mapper.BookCopyMapper;
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
 * Integration tests for the {@link BookCopyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookCopyResourceIT {

    private static final Integer DEFAULT_YEAR_PUBLISHED = 1;
    private static final Integer UPDATED_YEAR_PUBLISHED = 2;
    private static final Integer SMALLER_YEAR_PUBLISHED = 1 - 1;

    private static final String ENTITY_API_URL = "/api/book-copies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookCopyMockMvc;

    private BookCopy bookCopy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().yearPublished(DEFAULT_YEAR_PUBLISHED);
        return bookCopy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createUpdatedEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().yearPublished(UPDATED_YEAR_PUBLISHED);
        return bookCopy;
    }

    @BeforeEach
    public void initTest() {
        bookCopy = createEntity(em);
    }

    @Test
    @Transactional
    void createBookCopy() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);
        var returnedBookCopyDTO = om.readValue(
            restBookCopyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookCopyDTO.class
        );

        // Validate the BookCopy in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookCopy = bookCopyMapper.toEntity(returnedBookCopyDTO);
        assertBookCopyUpdatableFieldsEquals(returnedBookCopy, getPersistedBookCopy(returnedBookCopy));
    }

    @Test
    @Transactional
    void createBookCopyWithExistingId() throws Exception {
        // Create the BookCopy with an existing ID
        bookCopy.setId(1L);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookCopies() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].yearPublished").value(hasItem(DEFAULT_YEAR_PUBLISHED)));
    }

    @Test
    @Transactional
    void getBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get the bookCopy
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL_ID, bookCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCopy.getId().intValue()))
            .andExpect(jsonPath("$.yearPublished").value(DEFAULT_YEAR_PUBLISHED));
    }

    @Test
    @Transactional
    void getBookCopiesByIdFiltering() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        Long id = bookCopy.getId();

        defaultBookCopyFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBookCopyFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBookCopyFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished equals to
        defaultBookCopyFiltering("yearPublished.equals=" + DEFAULT_YEAR_PUBLISHED, "yearPublished.equals=" + UPDATED_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished in
        defaultBookCopyFiltering(
            "yearPublished.in=" + DEFAULT_YEAR_PUBLISHED + "," + UPDATED_YEAR_PUBLISHED,
            "yearPublished.in=" + UPDATED_YEAR_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished is not null
        defaultBookCopyFiltering("yearPublished.specified=true", "yearPublished.specified=false");
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished is greater than or equal to
        defaultBookCopyFiltering(
            "yearPublished.greaterThanOrEqual=" + DEFAULT_YEAR_PUBLISHED,
            "yearPublished.greaterThanOrEqual=" + UPDATED_YEAR_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished is less than or equal to
        defaultBookCopyFiltering(
            "yearPublished.lessThanOrEqual=" + DEFAULT_YEAR_PUBLISHED,
            "yearPublished.lessThanOrEqual=" + SMALLER_YEAR_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsLessThanSomething() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished is less than
        defaultBookCopyFiltering("yearPublished.lessThan=" + UPDATED_YEAR_PUBLISHED, "yearPublished.lessThan=" + DEFAULT_YEAR_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllBookCopiesByYearPublishedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList where yearPublished is greater than
        defaultBookCopyFiltering(
            "yearPublished.greaterThan=" + SMALLER_YEAR_PUBLISHED,
            "yearPublished.greaterThan=" + DEFAULT_YEAR_PUBLISHED
        );
    }

    @Test
    @Transactional
    void getAllBookCopiesByPublisherIsEqualToSomething() throws Exception {
        Publisher publisher;
        if (TestUtil.findAll(em, Publisher.class).isEmpty()) {
            bookCopyRepository.saveAndFlush(bookCopy);
            publisher = PublisherResourceIT.createEntity(em);
        } else {
            publisher = TestUtil.findAll(em, Publisher.class).get(0);
        }
        em.persist(publisher);
        em.flush();
        bookCopy.setPublisher(publisher);
        bookCopyRepository.saveAndFlush(bookCopy);
        Long publisherId = publisher.getId();
        // Get all the bookCopyList where publisher equals to publisherId
        defaultBookCopyShouldBeFound("publisherId.equals=" + publisherId);

        // Get all the bookCopyList where publisher equals to (publisherId + 1)
        defaultBookCopyShouldNotBeFound("publisherId.equals=" + (publisherId + 1));
    }

    @Test
    @Transactional
    void getAllBookCopiesByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            bookCopyRepository.saveAndFlush(bookCopy);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        bookCopy.setBook(book);
        bookCopyRepository.saveAndFlush(bookCopy);
        Long bookId = book.getId();
        // Get all the bookCopyList where book equals to bookId
        defaultBookCopyShouldBeFound("bookId.equals=" + bookId);

        // Get all the bookCopyList where book equals to (bookId + 1)
        defaultBookCopyShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultBookCopyFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBookCopyShouldBeFound(shouldBeFound);
        defaultBookCopyShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBookCopyShouldBeFound(String filter) throws Exception {
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].yearPublished").value(hasItem(DEFAULT_YEAR_PUBLISHED)));

        // Check, that the count call also returns 1
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBookCopyShouldNotBeFound(String filter) throws Exception {
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBookCopy() throws Exception {
        // Get the bookCopy
        restBookCopyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy
        BookCopy updatedBookCopy = bookCopyRepository.findById(bookCopy.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookCopy are not directly saved in db
        em.detach(updatedBookCopy);
        updatedBookCopy.yearPublished(UPDATED_YEAR_PUBLISHED);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(updatedBookCopy);

        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookCopyToMatchAllProperties(updatedBookCopy);
    }

    @Test
    @Transactional
    void putNonExistingBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCopyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBookCopy, bookCopy), getPersistedBookCopy(bookCopy));
    }

    @Test
    @Transactional
    void fullUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        partialUpdatedBookCopy.yearPublished(UPDATED_YEAR_PUBLISHED);

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookCopyUpdatableFieldsEquals(partialUpdatedBookCopy, getPersistedBookCopy(partialUpdatedBookCopy));
    }

    @Test
    @Transactional
    void patchNonExistingBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookCopy() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookCopy.setId(longCount.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookCopyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookCopy
        restBookCopyMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookCopy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookCopyRepository.count();
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

    protected BookCopy getPersistedBookCopy(BookCopy bookCopy) {
        return bookCopyRepository.findById(bookCopy.getId()).orElseThrow();
    }

    protected void assertPersistedBookCopyToMatchAllProperties(BookCopy expectedBookCopy) {
        assertBookCopyAllPropertiesEquals(expectedBookCopy, getPersistedBookCopy(expectedBookCopy));
    }

    protected void assertPersistedBookCopyToMatchUpdatableProperties(BookCopy expectedBookCopy) {
        assertBookCopyAllUpdatablePropertiesEquals(expectedBookCopy, getPersistedBookCopy(expectedBookCopy));
    }
}
