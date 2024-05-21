package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AuthorAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.repository.AuthorRepository;
import com.mycompany.myapp.service.AuthorService;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.mapper.AuthorMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AuthorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AuthorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AuthorRepository authorRepository;

    @Mock
    private AuthorRepository authorRepositoryMock;

    @Autowired
    private AuthorMapper authorMapper;

    @Mock
    private AuthorService authorServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAuthorMockMvc;

    private Author author;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createEntity(EntityManager em) {
        Author author = new Author().name(DEFAULT_NAME);
        return author;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createUpdatedEntity(EntityManager em) {
        Author author = new Author().name(UPDATED_NAME);
        return author;
    }

    @BeforeEach
    public void initTest() {
        author = createEntity(em);
    }

    @Test
    @Transactional
    void createAuthor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);
        var returnedAuthorDTO = om.readValue(
            restAuthorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AuthorDTO.class
        );

        // Validate the Author in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAuthor = authorMapper.toEntity(returnedAuthorDTO);
        assertAuthorUpdatableFieldsEquals(returnedAuthor, getPersistedAuthor(returnedAuthor));
    }

    @Test
    @Transactional
    void createAuthorWithExistingId() throws Exception {
        // Create the Author with an existing ID
        author.setId(1L);
        AuthorDTO authorDTO = authorMapper.toDto(author);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuthorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAuthors() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuthorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(authorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAuthorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(authorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAuthorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(authorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAuthorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(authorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get the author
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL_ID, author.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(author.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getAuthorsByIdFiltering() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        Long id = author.getId();

        defaultAuthorFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAuthorFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAuthorFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name equals to
        defaultAuthorFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name in
        defaultAuthorFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name is not null
        defaultAuthorFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllAuthorsByNameContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name contains
        defaultAuthorFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        // Get all the authorList where name does not contain
        defaultAuthorFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllAuthorsByBookIsEqualToSomething() throws Exception {
        Book book;
        if (TestUtil.findAll(em, Book.class).isEmpty()) {
            authorRepository.saveAndFlush(author);
            book = BookResourceIT.createEntity(em);
        } else {
            book = TestUtil.findAll(em, Book.class).get(0);
        }
        em.persist(book);
        em.flush();
        author.addBook(book);
        authorRepository.saveAndFlush(author);
        Long bookId = book.getId();
        // Get all the authorList where book equals to bookId
        defaultAuthorShouldBeFound("bookId.equals=" + bookId);

        // Get all the authorList where book equals to (bookId + 1)
        defaultAuthorShouldNotBeFound("bookId.equals=" + (bookId + 1));
    }

    private void defaultAuthorFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAuthorShouldBeFound(shouldBeFound);
        defaultAuthorShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAuthorShouldBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(author.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAuthorShouldNotBeFound(String filter) throws Exception {
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAuthorMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAuthor() throws Exception {
        // Get the author
        restAuthorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author
        Author updatedAuthor = authorRepository.findById(author.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAuthor are not directly saved in db
        em.detach(updatedAuthor);
        updatedAuthor.name(UPDATED_NAME);
        AuthorDTO authorDTO = authorMapper.toDto(updatedAuthor);

        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAuthorToMatchAllProperties(updatedAuthor);
    }

    @Test
    @Transactional
    void putNonExistingAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, authorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(authorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(authorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthor))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAuthor, author), getPersistedAuthor(author));
    }

    @Test
    @Transactional
    void fullUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        partialUpdatedAuthor.name(UPDATED_NAME);

        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAuthor))
            )
            .andExpect(status().isOk());

        // Validate the Author in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAuthorUpdatableFieldsEquals(partialUpdatedAuthor, getPersistedAuthor(partialUpdatedAuthor));
    }

    @Test
    @Transactional
    void patchNonExistingAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, authorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(authorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAuthor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        author.setId(longCount.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAuthorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(authorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Author in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAuthor() throws Exception {
        // Initialize the database
        authorRepository.saveAndFlush(author);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the author
        restAuthorMockMvc
            .perform(delete(ENTITY_API_URL_ID, author.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return authorRepository.count();
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

    protected Author getPersistedAuthor(Author author) {
        return authorRepository.findById(author.getId()).orElseThrow();
    }

    protected void assertPersistedAuthorToMatchAllProperties(Author expectedAuthor) {
        assertAuthorAllPropertiesEquals(expectedAuthor, getPersistedAuthor(expectedAuthor));
    }

    protected void assertPersistedAuthorToMatchUpdatableProperties(Author expectedAuthor) {
        assertAuthorAllUpdatablePropertiesEquals(expectedAuthor, getPersistedAuthor(expectedAuthor));
    }
}
