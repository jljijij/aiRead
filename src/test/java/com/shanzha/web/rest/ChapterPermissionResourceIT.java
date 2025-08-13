package com.shanzha.web.rest;

import static com.shanzha.domain.ChapterPermissionAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.ChapterPermission;
import com.shanzha.repository.ChapterPermissionRepository;
import com.shanzha.service.dto.ChapterPermissionDTO;
import com.shanzha.service.mapper.ChapterPermissionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ChapterPermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChapterPermissionResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_CHAPTER_ID = 1L;
    private static final Long UPDATED_CHAPTER_ID = 2L;

    private static final Boolean DEFAULT_CAN_READ = false;
    private static final Boolean UPDATED_CAN_READ = true;

    private static final Boolean DEFAULT_CAN_DOWNLOAD = false;
    private static final Boolean UPDATED_CAN_DOWNLOAD = true;

    private static final Boolean DEFAULT_CAN_COMMENT = false;
    private static final Boolean UPDATED_CAN_COMMENT = true;

    private static final String ENTITY_API_URL = "/api/chapter-permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterPermissionRepository chapterPermissionRepository;

    @Autowired
    private ChapterPermissionMapper chapterPermissionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterPermissionMockMvc;

    private ChapterPermission chapterPermission;

    private ChapterPermission insertedChapterPermission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPermission createEntity() {
        return new ChapterPermission()
            .userId(DEFAULT_USER_ID)
            .chapterId(DEFAULT_CHAPTER_ID)
            .canRead(DEFAULT_CAN_READ)
            .canDownload(DEFAULT_CAN_DOWNLOAD)
            .canComment(DEFAULT_CAN_COMMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPermission createUpdatedEntity() {
        return new ChapterPermission()
            .userId(UPDATED_USER_ID)
            .chapterId(UPDATED_CHAPTER_ID)
            .canRead(UPDATED_CAN_READ)
            .canDownload(UPDATED_CAN_DOWNLOAD)
            .canComment(UPDATED_CAN_COMMENT);
    }

    @BeforeEach
    void initTest() {
        chapterPermission = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChapterPermission != null) {
            chapterPermissionRepository.delete(insertedChapterPermission);
            insertedChapterPermission = null;
        }
    }

    @Test
    @Transactional
    void createChapterPermission() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);
        var returnedChapterPermissionDTO = om.readValue(
            restChapterPermissionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterPermissionDTO.class
        );

        // Validate the ChapterPermission in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapterPermission = chapterPermissionMapper.toEntity(returnedChapterPermissionDTO);
        assertChapterPermissionUpdatableFieldsEquals(returnedChapterPermission, getPersistedChapterPermission(returnedChapterPermission));

        insertedChapterPermission = returnedChapterPermission;
    }

    @Test
    @Transactional
    void createChapterPermissionWithExistingId() throws Exception {
        // Create the ChapterPermission with an existing ID
        chapterPermission.setId(1L);
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPermission.setUserId(null);

        // Create the ChapterPermission, which fails.
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        restChapterPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChapterIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPermission.setChapterId(null);

        // Create the ChapterPermission, which fails.
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        restChapterPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCanReadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPermission.setCanRead(null);

        // Create the ChapterPermission, which fails.
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        restChapterPermissionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapterPermissions() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        // Get all the chapterPermissionList
        restChapterPermissionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapterPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].chapterId").value(hasItem(DEFAULT_CHAPTER_ID.intValue())))
            .andExpect(jsonPath("$.[*].canRead").value(hasItem(DEFAULT_CAN_READ)))
            .andExpect(jsonPath("$.[*].canDownload").value(hasItem(DEFAULT_CAN_DOWNLOAD)))
            .andExpect(jsonPath("$.[*].canComment").value(hasItem(DEFAULT_CAN_COMMENT)));
    }

    @Test
    @Transactional
    void getChapterPermission() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        // Get the chapterPermission
        restChapterPermissionMockMvc
            .perform(get(ENTITY_API_URL_ID, chapterPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapterPermission.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.chapterId").value(DEFAULT_CHAPTER_ID.intValue()))
            .andExpect(jsonPath("$.canRead").value(DEFAULT_CAN_READ))
            .andExpect(jsonPath("$.canDownload").value(DEFAULT_CAN_DOWNLOAD))
            .andExpect(jsonPath("$.canComment").value(DEFAULT_CAN_COMMENT));
    }

    @Test
    @Transactional
    void getNonExistingChapterPermission() throws Exception {
        // Get the chapterPermission
        restChapterPermissionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapterPermission() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPermission
        ChapterPermission updatedChapterPermission = chapterPermissionRepository.findById(chapterPermission.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapterPermission are not directly saved in db
        em.detach(updatedChapterPermission);
        updatedChapterPermission
            .userId(UPDATED_USER_ID)
            .chapterId(UPDATED_CHAPTER_ID)
            .canRead(UPDATED_CAN_READ)
            .canDownload(UPDATED_CAN_DOWNLOAD)
            .canComment(UPDATED_CAN_COMMENT);
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(updatedChapterPermission);

        restChapterPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPermissionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterPermissionToMatchAllProperties(updatedChapterPermission);
    }

    @Test
    @Transactional
    void putNonExistingChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPermissionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterPermissionWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPermission using partial update
        ChapterPermission partialUpdatedChapterPermission = new ChapterPermission();
        partialUpdatedChapterPermission.setId(chapterPermission.getId());

        partialUpdatedChapterPermission.canDownload(UPDATED_CAN_DOWNLOAD).canComment(UPDATED_CAN_COMMENT);

        restChapterPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPermission))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPermissionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChapterPermission, chapterPermission),
            getPersistedChapterPermission(chapterPermission)
        );
    }

    @Test
    @Transactional
    void fullUpdateChapterPermissionWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPermission using partial update
        ChapterPermission partialUpdatedChapterPermission = new ChapterPermission();
        partialUpdatedChapterPermission.setId(chapterPermission.getId());

        partialUpdatedChapterPermission
            .userId(UPDATED_USER_ID)
            .chapterId(UPDATED_CHAPTER_ID)
            .canRead(UPDATED_CAN_READ)
            .canDownload(UPDATED_CAN_DOWNLOAD)
            .canComment(UPDATED_CAN_COMMENT);

        restChapterPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPermission.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPermission))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPermission in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPermissionUpdatableFieldsEquals(
            partialUpdatedChapterPermission,
            getPersistedChapterPermission(partialUpdatedChapterPermission)
        );
    }

    @Test
    @Transactional
    void patchNonExistingChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterPermissionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPermissionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapterPermission() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPermission.setId(longCount.incrementAndGet());

        // Create the ChapterPermission
        ChapterPermissionDTO chapterPermissionDTO = chapterPermissionMapper.toDto(chapterPermission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPermissionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterPermissionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPermission in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapterPermission() throws Exception {
        // Initialize the database
        insertedChapterPermission = chapterPermissionRepository.saveAndFlush(chapterPermission);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapterPermission
        restChapterPermissionMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapterPermission.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterPermissionRepository.count();
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

    protected ChapterPermission getPersistedChapterPermission(ChapterPermission chapterPermission) {
        return chapterPermissionRepository.findById(chapterPermission.getId()).orElseThrow();
    }

    protected void assertPersistedChapterPermissionToMatchAllProperties(ChapterPermission expectedChapterPermission) {
        assertChapterPermissionAllPropertiesEquals(expectedChapterPermission, getPersistedChapterPermission(expectedChapterPermission));
    }

    protected void assertPersistedChapterPermissionToMatchUpdatableProperties(ChapterPermission expectedChapterPermission) {
        assertChapterPermissionAllUpdatablePropertiesEquals(
            expectedChapterPermission,
            getPersistedChapterPermission(expectedChapterPermission)
        );
    }
}
