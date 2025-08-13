package com.shanzha.web.rest;

import static com.shanzha.domain.ChapterPackageAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.ChapterPackage;
import com.shanzha.repository.ChapterPackageRepository;
import com.shanzha.service.dto.ChapterPackageDTO;
import com.shanzha.service.mapper.ChapterPackageMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChapterPackageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChapterPackageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/chapter-packages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterPackageRepository chapterPackageRepository;

    @Autowired
    private ChapterPackageMapper chapterPackageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterPackageMockMvc;

    private ChapterPackage chapterPackage;

    private ChapterPackage insertedChapterPackage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPackage createEntity() {
        return new ChapterPackage().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPackage createUpdatedEntity() {
        return new ChapterPackage().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        chapterPackage = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChapterPackage != null) {
            chapterPackageRepository.delete(insertedChapterPackage);
            insertedChapterPackage = null;
        }
    }

    @Test
    @Transactional
    void createChapterPackage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);
        var returnedChapterPackageDTO = om.readValue(
            restChapterPackageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterPackageDTO.class
        );

        // Validate the ChapterPackage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapterPackage = chapterPackageMapper.toEntity(returnedChapterPackageDTO);
        assertChapterPackageUpdatableFieldsEquals(returnedChapterPackage, getPersistedChapterPackage(returnedChapterPackage));

        insertedChapterPackage = returnedChapterPackage;
    }

    @Test
    @Transactional
    void createChapterPackageWithExistingId() throws Exception {
        // Create the ChapterPackage with an existing ID
        chapterPackage.setId(1L);
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterPackageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPackage.setName(null);

        // Create the ChapterPackage, which fails.
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        restChapterPackageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapterPackages() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        // Get all the chapterPackageList
        restChapterPackageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapterPackage.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getChapterPackage() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        // Get the chapterPackage
        restChapterPackageMockMvc
            .perform(get(ENTITY_API_URL_ID, chapterPackage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapterPackage.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChapterPackage() throws Exception {
        // Get the chapterPackage
        restChapterPackageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapterPackage() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackage
        ChapterPackage updatedChapterPackage = chapterPackageRepository.findById(chapterPackage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapterPackage are not directly saved in db
        em.detach(updatedChapterPackage);
        updatedChapterPackage.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(updatedChapterPackage);

        restChapterPackageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPackageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterPackageToMatchAllProperties(updatedChapterPackage);
    }

    @Test
    @Transactional
    void putNonExistingChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPackageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterPackageWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackage using partial update
        ChapterPackage partialUpdatedChapterPackage = new ChapterPackage();
        partialUpdatedChapterPackage.setId(chapterPackage.getId());

        partialUpdatedChapterPackage.description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);

        restChapterPackageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPackage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPackage))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPackageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChapterPackage, chapterPackage),
            getPersistedChapterPackage(chapterPackage)
        );
    }

    @Test
    @Transactional
    void fullUpdateChapterPackageWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackage using partial update
        ChapterPackage partialUpdatedChapterPackage = new ChapterPackage();
        partialUpdatedChapterPackage.setId(chapterPackage.getId());

        partialUpdatedChapterPackage.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).createdAt(UPDATED_CREATED_AT);

        restChapterPackageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPackage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPackage))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPackageUpdatableFieldsEquals(partialUpdatedChapterPackage, getPersistedChapterPackage(partialUpdatedChapterPackage));
    }

    @Test
    @Transactional
    void patchNonExistingChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterPackageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPackageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPackageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapterPackage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackage.setId(longCount.incrementAndGet());

        // Create the ChapterPackage
        ChapterPackageDTO chapterPackageDTO = chapterPackageMapper.toDto(chapterPackage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterPackageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPackage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapterPackage() throws Exception {
        // Initialize the database
        insertedChapterPackage = chapterPackageRepository.saveAndFlush(chapterPackage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapterPackage
        restChapterPackageMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapterPackage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterPackageRepository.count();
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

    protected ChapterPackage getPersistedChapterPackage(ChapterPackage chapterPackage) {
        return chapterPackageRepository.findById(chapterPackage.getId()).orElseThrow();
    }

    protected void assertPersistedChapterPackageToMatchAllProperties(ChapterPackage expectedChapterPackage) {
        assertChapterPackageAllPropertiesEquals(expectedChapterPackage, getPersistedChapterPackage(expectedChapterPackage));
    }

    protected void assertPersistedChapterPackageToMatchUpdatableProperties(ChapterPackage expectedChapterPackage) {
        assertChapterPackageAllUpdatablePropertiesEquals(expectedChapterPackage, getPersistedChapterPackage(expectedChapterPackage));
    }
}
