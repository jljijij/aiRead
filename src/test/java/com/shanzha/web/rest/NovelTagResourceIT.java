package com.shanzha.web.rest;

import static com.shanzha.domain.NovelTagAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.NovelTag;
import com.shanzha.repository.NovelTagRepository;
import com.shanzha.service.dto.NovelTagDTO;
import com.shanzha.service.mapper.NovelTagMapper;
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
 * Integration tests for the {@link NovelTagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NovelTagResourceIT {

    private static final String DEFAULT_TAG_ID = "AAAAAAAAAA";
    private static final String UPDATED_TAG_ID = "BBBBBBBBBB";

    private static final String DEFAULT_TAG_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TAG_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_HOT = false;
    private static final Boolean UPDATED_IS_HOT = true;

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/novel-tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NovelTagRepository novelTagRepository;

    @Autowired
    private NovelTagMapper novelTagMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNovelTagMockMvc;

    private NovelTag novelTag;

    private NovelTag insertedNovelTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NovelTag createEntity() {
        return new NovelTag()
            .tagId(DEFAULT_TAG_ID)
            .tagName(DEFAULT_TAG_NAME)
            .category(DEFAULT_CATEGORY)
            .isHot(DEFAULT_IS_HOT)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NovelTag createUpdatedEntity() {
        return new NovelTag()
            .tagId(UPDATED_TAG_ID)
            .tagName(UPDATED_TAG_NAME)
            .category(UPDATED_CATEGORY)
            .isHot(UPDATED_IS_HOT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
    }

    @BeforeEach
    void initTest() {
        novelTag = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNovelTag != null) {
            novelTagRepository.delete(insertedNovelTag);
            insertedNovelTag = null;
        }
    }

    @Test
    @Transactional
    void createNovelTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);
        var returnedNovelTagDTO = om.readValue(
            restNovelTagMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NovelTagDTO.class
        );

        // Validate the NovelTag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNovelTag = novelTagMapper.toEntity(returnedNovelTagDTO);
        assertNovelTagUpdatableFieldsEquals(returnedNovelTag, getPersistedNovelTag(returnedNovelTag));

        insertedNovelTag = returnedNovelTag;
    }

    @Test
    @Transactional
    void createNovelTagWithExistingId() throws Exception {
        // Create the NovelTag with an existing ID
        novelTag.setId(1L);
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTagIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novelTag.setTagId(null);

        // Create the NovelTag, which fails.
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTagNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novelTag.setTagName(null);

        // Create the NovelTag, which fails.
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novelTag.setCategory(null);

        // Create the NovelTag, which fails.
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novelTag.setCreateTime(null);

        // Create the NovelTag, which fails.
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novelTag.setUpdateTime(null);

        // Create the NovelTag, which fails.
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        restNovelTagMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNovelTags() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        // Get all the novelTagList
        restNovelTagMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(novelTag.getId().intValue())))
            .andExpect(jsonPath("$.[*].tagId").value(hasItem(DEFAULT_TAG_ID)))
            .andExpect(jsonPath("$.[*].tagName").value(hasItem(DEFAULT_TAG_NAME)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].isHot").value(hasItem(DEFAULT_IS_HOT)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())));
    }

    @Test
    @Transactional
    void getNovelTag() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        // Get the novelTag
        restNovelTagMockMvc
            .perform(get(ENTITY_API_URL_ID, novelTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(novelTag.getId().intValue()))
            .andExpect(jsonPath("$.tagId").value(DEFAULT_TAG_ID))
            .andExpect(jsonPath("$.tagName").value(DEFAULT_TAG_NAME))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.isHot").value(DEFAULT_IS_HOT))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNovelTag() throws Exception {
        // Get the novelTag
        restNovelTagMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNovelTag() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novelTag
        NovelTag updatedNovelTag = novelTagRepository.findById(novelTag.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNovelTag are not directly saved in db
        em.detach(updatedNovelTag);
        updatedNovelTag
            .tagId(UPDATED_TAG_ID)
            .tagName(UPDATED_TAG_NAME)
            .category(UPDATED_CATEGORY)
            .isHot(UPDATED_IS_HOT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(updatedNovelTag);

        restNovelTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, novelTagDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(novelTagDTO))
            )
            .andExpect(status().isOk());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNovelTagToMatchAllProperties(updatedNovelTag);
    }

    @Test
    @Transactional
    void putNonExistingNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, novelTagDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(novelTagDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(novelTagDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNovelTagWithPatch() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novelTag using partial update
        NovelTag partialUpdatedNovelTag = new NovelTag();
        partialUpdatedNovelTag.setId(novelTag.getId());

        partialUpdatedNovelTag.tagId(UPDATED_TAG_ID).createTime(UPDATED_CREATE_TIME);

        restNovelTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNovelTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNovelTag))
            )
            .andExpect(status().isOk());

        // Validate the NovelTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNovelTagUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedNovelTag, novelTag), getPersistedNovelTag(novelTag));
    }

    @Test
    @Transactional
    void fullUpdateNovelTagWithPatch() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novelTag using partial update
        NovelTag partialUpdatedNovelTag = new NovelTag();
        partialUpdatedNovelTag.setId(novelTag.getId());

        partialUpdatedNovelTag
            .tagId(UPDATED_TAG_ID)
            .tagName(UPDATED_TAG_NAME)
            .category(UPDATED_CATEGORY)
            .isHot(UPDATED_IS_HOT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);

        restNovelTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNovelTag.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNovelTag))
            )
            .andExpect(status().isOk());

        // Validate the NovelTag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNovelTagUpdatableFieldsEquals(partialUpdatedNovelTag, getPersistedNovelTag(partialUpdatedNovelTag));
    }

    @Test
    @Transactional
    void patchNonExistingNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, novelTagDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(novelTagDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(novelTagDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNovelTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novelTag.setId(longCount.incrementAndGet());

        // Create the NovelTag
        NovelTagDTO novelTagDTO = novelTagMapper.toDto(novelTag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelTagMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(novelTagDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NovelTag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNovelTag() throws Exception {
        // Initialize the database
        insertedNovelTag = novelTagRepository.saveAndFlush(novelTag);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the novelTag
        restNovelTagMockMvc
            .perform(delete(ENTITY_API_URL_ID, novelTag.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return novelTagRepository.count();
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

    protected NovelTag getPersistedNovelTag(NovelTag novelTag) {
        return novelTagRepository.findById(novelTag.getId()).orElseThrow();
    }

    protected void assertPersistedNovelTagToMatchAllProperties(NovelTag expectedNovelTag) {
        assertNovelTagAllPropertiesEquals(expectedNovelTag, getPersistedNovelTag(expectedNovelTag));
    }

    protected void assertPersistedNovelTagToMatchUpdatableProperties(NovelTag expectedNovelTag) {
        assertNovelTagAllUpdatablePropertiesEquals(expectedNovelTag, getPersistedNovelTag(expectedNovelTag));
    }
}
