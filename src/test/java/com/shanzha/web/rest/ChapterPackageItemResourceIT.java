package com.shanzha.web.rest;

import static com.shanzha.domain.ChapterPackageItemAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.ChapterPackageItem;
import com.shanzha.repository.ChapterPackageItemRepository;
import com.shanzha.service.dto.ChapterPackageItemDTO;
import com.shanzha.service.mapper.ChapterPackageItemMapper;
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
 * Integration tests for the {@link ChapterPackageItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ChapterPackageItemResourceIT {

    private static final Long DEFAULT_PACKAGE_ID = 1L;
    private static final Long UPDATED_PACKAGE_ID = 2L;

    private static final Long DEFAULT_CHAPTER_ID = 1L;
    private static final Long UPDATED_CHAPTER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/chapter-package-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterPackageItemRepository chapterPackageItemRepository;

    @Autowired
    private ChapterPackageItemMapper chapterPackageItemMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterPackageItemMockMvc;

    private ChapterPackageItem chapterPackageItem;

    private ChapterPackageItem insertedChapterPackageItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPackageItem createEntity() {
        return new ChapterPackageItem().packageId(DEFAULT_PACKAGE_ID).chapterId(DEFAULT_CHAPTER_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterPackageItem createUpdatedEntity() {
        return new ChapterPackageItem().packageId(UPDATED_PACKAGE_ID).chapterId(UPDATED_CHAPTER_ID);
    }

    @BeforeEach
    void initTest() {
        chapterPackageItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChapterPackageItem != null) {
            chapterPackageItemRepository.delete(insertedChapterPackageItem);
            insertedChapterPackageItem = null;
        }
    }

    @Test
    @Transactional
    void createChapterPackageItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);
        var returnedChapterPackageItemDTO = om.readValue(
            restChapterPackageItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterPackageItemDTO.class
        );

        // Validate the ChapterPackageItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapterPackageItem = chapterPackageItemMapper.toEntity(returnedChapterPackageItemDTO);
        assertChapterPackageItemUpdatableFieldsEquals(
            returnedChapterPackageItem,
            getPersistedChapterPackageItem(returnedChapterPackageItem)
        );

        insertedChapterPackageItem = returnedChapterPackageItem;
    }

    @Test
    @Transactional
    void createChapterPackageItemWithExistingId() throws Exception {
        // Create the ChapterPackageItem with an existing ID
        chapterPackageItem.setId(1L);
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterPackageItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPackageIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPackageItem.setPackageId(null);

        // Create the ChapterPackageItem, which fails.
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        restChapterPackageItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChapterIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterPackageItem.setChapterId(null);

        // Create the ChapterPackageItem, which fails.
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        restChapterPackageItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapterPackageItems() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        // Get all the chapterPackageItemList
        restChapterPackageItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapterPackageItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].packageId").value(hasItem(DEFAULT_PACKAGE_ID.intValue())))
            .andExpect(jsonPath("$.[*].chapterId").value(hasItem(DEFAULT_CHAPTER_ID.intValue())));
    }

    @Test
    @Transactional
    void getChapterPackageItem() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        // Get the chapterPackageItem
        restChapterPackageItemMockMvc
            .perform(get(ENTITY_API_URL_ID, chapterPackageItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapterPackageItem.getId().intValue()))
            .andExpect(jsonPath("$.packageId").value(DEFAULT_PACKAGE_ID.intValue()))
            .andExpect(jsonPath("$.chapterId").value(DEFAULT_CHAPTER_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingChapterPackageItem() throws Exception {
        // Get the chapterPackageItem
        restChapterPackageItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapterPackageItem() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackageItem
        ChapterPackageItem updatedChapterPackageItem = chapterPackageItemRepository.findById(chapterPackageItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapterPackageItem are not directly saved in db
        em.detach(updatedChapterPackageItem);
        updatedChapterPackageItem.packageId(UPDATED_PACKAGE_ID).chapterId(UPDATED_CHAPTER_ID);
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(updatedChapterPackageItem);

        restChapterPackageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPackageItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterPackageItemToMatchAllProperties(updatedChapterPackageItem);
    }

    @Test
    @Transactional
    void putNonExistingChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterPackageItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterPackageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterPackageItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterPackageItemWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackageItem using partial update
        ChapterPackageItem partialUpdatedChapterPackageItem = new ChapterPackageItem();
        partialUpdatedChapterPackageItem.setId(chapterPackageItem.getId());

        partialUpdatedChapterPackageItem.packageId(UPDATED_PACKAGE_ID);

        restChapterPackageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPackageItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPackageItem))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackageItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPackageItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChapterPackageItem, chapterPackageItem),
            getPersistedChapterPackageItem(chapterPackageItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateChapterPackageItemWithPatch() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterPackageItem using partial update
        ChapterPackageItem partialUpdatedChapterPackageItem = new ChapterPackageItem();
        partialUpdatedChapterPackageItem.setId(chapterPackageItem.getId());

        partialUpdatedChapterPackageItem.packageId(UPDATED_PACKAGE_ID).chapterId(UPDATED_CHAPTER_ID);

        restChapterPackageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterPackageItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterPackageItem))
            )
            .andExpect(status().isOk());

        // Validate the ChapterPackageItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterPackageItemUpdatableFieldsEquals(
            partialUpdatedChapterPackageItem,
            getPersistedChapterPackageItem(partialUpdatedChapterPackageItem)
        );
    }

    @Test
    @Transactional
    void patchNonExistingChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterPackageItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPackageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterPackageItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapterPackageItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterPackageItem.setId(longCount.incrementAndGet());

        // Create the ChapterPackageItem
        ChapterPackageItemDTO chapterPackageItemDTO = chapterPackageItemMapper.toDto(chapterPackageItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterPackageItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterPackageItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterPackageItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapterPackageItem() throws Exception {
        // Initialize the database
        insertedChapterPackageItem = chapterPackageItemRepository.saveAndFlush(chapterPackageItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapterPackageItem
        restChapterPackageItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapterPackageItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterPackageItemRepository.count();
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

    protected ChapterPackageItem getPersistedChapterPackageItem(ChapterPackageItem chapterPackageItem) {
        return chapterPackageItemRepository.findById(chapterPackageItem.getId()).orElseThrow();
    }

    protected void assertPersistedChapterPackageItemToMatchAllProperties(ChapterPackageItem expectedChapterPackageItem) {
        assertChapterPackageItemAllPropertiesEquals(expectedChapterPackageItem, getPersistedChapterPackageItem(expectedChapterPackageItem));
    }

    protected void assertPersistedChapterPackageItemToMatchUpdatableProperties(ChapterPackageItem expectedChapterPackageItem) {
        assertChapterPackageItemAllUpdatablePropertiesEquals(
            expectedChapterPackageItem,
            getPersistedChapterPackageItem(expectedChapterPackageItem)
        );
    }
}
