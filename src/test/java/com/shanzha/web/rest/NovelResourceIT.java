package com.shanzha.web.rest;

import static com.shanzha.domain.NovelAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.Novel;
import com.shanzha.repository.NovelRepository;
import com.shanzha.service.dto.NovelDTO;
import com.shanzha.service.mapper.NovelMapper;
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
 * Integration tests for the {@link NovelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NovelResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_AUTHOR_ID = 1L;
    private static final Long UPDATED_AUTHOR_ID = 2L;

    private static final String DEFAULT_COVER_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_CATEGORY_ID = 1;
    private static final Integer UPDATED_CATEGORY_ID = 2;

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final Integer DEFAULT_WORD_COUNT = 1;
    private static final Integer UPDATED_WORD_COUNT = 2;

    private static final Integer DEFAULT_CHAPTER_COUNT = 1;
    private static final Integer UPDATED_CHAPTER_COUNT = 2;

    private static final Integer DEFAULT_STATUS = 1;
    private static final Integer UPDATED_STATUS = 2;

    private static final Boolean DEFAULT_IS_VIP = false;
    private static final Boolean UPDATED_IS_VIP = true;

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/novels";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NovelRepository novelRepository;

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNovelMockMvc;

    private Novel novel;

    private Novel insertedNovel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Novel createEntity() {
        return new Novel()
            .title(DEFAULT_TITLE)
            .authorId(DEFAULT_AUTHOR_ID)
            .coverUrl(DEFAULT_COVER_URL)
            .description(DEFAULT_DESCRIPTION)
            .categoryId(DEFAULT_CATEGORY_ID)
            .tags(DEFAULT_TAGS)
            .wordCount(DEFAULT_WORD_COUNT)
            .chapterCount(DEFAULT_CHAPTER_COUNT)
            .status(DEFAULT_STATUS)
            .isVip(DEFAULT_IS_VIP)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Novel createUpdatedEntity() {
        return new Novel()
            .title(UPDATED_TITLE)
            .authorId(UPDATED_AUTHOR_ID)
            .coverUrl(UPDATED_COVER_URL)
            .description(UPDATED_DESCRIPTION)
            .categoryId(UPDATED_CATEGORY_ID)
            .tags(UPDATED_TAGS)
            .wordCount(UPDATED_WORD_COUNT)
            .chapterCount(UPDATED_CHAPTER_COUNT)
            .status(UPDATED_STATUS)
            .isVip(UPDATED_IS_VIP)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
    }

    @BeforeEach
    void initTest() {
        novel = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNovel != null) {
            novelRepository.delete(insertedNovel);
            insertedNovel = null;
        }
    }

    @Test
    @Transactional
    void createNovel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);
        var returnedNovelDTO = om.readValue(
            restNovelMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NovelDTO.class
        );

        // Validate the Novel in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNovel = novelMapper.toEntity(returnedNovelDTO);
        assertNovelUpdatableFieldsEquals(returnedNovel, getPersistedNovel(returnedNovel));

        insertedNovel = returnedNovel;
    }

    @Test
    @Transactional
    void createNovelWithExistingId() throws Exception {
        // Create the Novel with an existing ID
        novel.setId(1L);
        NovelDTO novelDTO = novelMapper.toDto(novel);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setTitle(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAuthorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setAuthorId(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setCategoryId(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setStatus(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setCreateTime(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        novel.setUpdateTime(null);

        // Create the Novel, which fails.
        NovelDTO novelDTO = novelMapper.toDto(novel);

        restNovelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNovels() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        // Get all the novelList
        restNovelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(novel.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].coverUrl").value(hasItem(DEFAULT_COVER_URL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].categoryId").value(hasItem(DEFAULT_CATEGORY_ID)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].wordCount").value(hasItem(DEFAULT_WORD_COUNT)))
            .andExpect(jsonPath("$.[*].chapterCount").value(hasItem(DEFAULT_CHAPTER_COUNT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].isVip").value(hasItem(DEFAULT_IS_VIP)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())));
    }

    @Test
    @Transactional
    void getNovel() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        // Get the novel
        restNovelMockMvc
            .perform(get(ENTITY_API_URL_ID, novel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(novel.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.authorId").value(DEFAULT_AUTHOR_ID.intValue()))
            .andExpect(jsonPath("$.coverUrl").value(DEFAULT_COVER_URL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.categoryId").value(DEFAULT_CATEGORY_ID))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.wordCount").value(DEFAULT_WORD_COUNT))
            .andExpect(jsonPath("$.chapterCount").value(DEFAULT_CHAPTER_COUNT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.isVip").value(DEFAULT_IS_VIP))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNovel() throws Exception {
        // Get the novel
        restNovelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNovel() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novel
        Novel updatedNovel = novelRepository.findById(novel.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNovel are not directly saved in db
        em.detach(updatedNovel);
        updatedNovel
            .title(UPDATED_TITLE)
            .authorId(UPDATED_AUTHOR_ID)
            .coverUrl(UPDATED_COVER_URL)
            .description(UPDATED_DESCRIPTION)
            .categoryId(UPDATED_CATEGORY_ID)
            .tags(UPDATED_TAGS)
            .wordCount(UPDATED_WORD_COUNT)
            .chapterCount(UPDATED_CHAPTER_COUNT)
            .status(UPDATED_STATUS)
            .isVip(UPDATED_IS_VIP)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        NovelDTO novelDTO = novelMapper.toDto(updatedNovel);

        restNovelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, novelDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO))
            )
            .andExpect(status().isOk());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNovelToMatchAllProperties(updatedNovel);
    }

    @Test
    @Transactional
    void putNonExistingNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, novelDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(novelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNovelWithPatch() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novel using partial update
        Novel partialUpdatedNovel = new Novel();
        partialUpdatedNovel.setId(novel.getId());

        partialUpdatedNovel
            .title(UPDATED_TITLE)
            .authorId(UPDATED_AUTHOR_ID)
            .description(UPDATED_DESCRIPTION)
            .categoryId(UPDATED_CATEGORY_ID)
            .tags(UPDATED_TAGS)
            .wordCount(UPDATED_WORD_COUNT)
            .chapterCount(UPDATED_CHAPTER_COUNT)
            .status(UPDATED_STATUS)
            .isVip(UPDATED_IS_VIP);

        restNovelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNovel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNovel))
            )
            .andExpect(status().isOk());

        // Validate the Novel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNovelUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedNovel, novel), getPersistedNovel(novel));
    }

    @Test
    @Transactional
    void fullUpdateNovelWithPatch() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the novel using partial update
        Novel partialUpdatedNovel = new Novel();
        partialUpdatedNovel.setId(novel.getId());

        partialUpdatedNovel
            .title(UPDATED_TITLE)
            .authorId(UPDATED_AUTHOR_ID)
            .coverUrl(UPDATED_COVER_URL)
            .description(UPDATED_DESCRIPTION)
            .categoryId(UPDATED_CATEGORY_ID)
            .tags(UPDATED_TAGS)
            .wordCount(UPDATED_WORD_COUNT)
            .chapterCount(UPDATED_CHAPTER_COUNT)
            .status(UPDATED_STATUS)
            .isVip(UPDATED_IS_VIP)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);

        restNovelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNovel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNovel))
            )
            .andExpect(status().isOk());

        // Validate the Novel in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNovelUpdatableFieldsEquals(partialUpdatedNovel, getPersistedNovel(partialUpdatedNovel));
    }

    @Test
    @Transactional
    void patchNonExistingNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, novelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(novelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(novelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNovel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        novel.setId(longCount.incrementAndGet());

        // Create the Novel
        NovelDTO novelDTO = novelMapper.toDto(novel);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNovelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(novelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Novel in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNovel() throws Exception {
        // Initialize the database
        insertedNovel = novelRepository.saveAndFlush(novel);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the novel
        restNovelMockMvc
            .perform(delete(ENTITY_API_URL_ID, novel.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return novelRepository.count();
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

    protected Novel getPersistedNovel(Novel novel) {
        return novelRepository.findById(novel.getId()).orElseThrow();
    }

    protected void assertPersistedNovelToMatchAllProperties(Novel expectedNovel) {
        assertNovelAllPropertiesEquals(expectedNovel, getPersistedNovel(expectedNovel));
    }

    protected void assertPersistedNovelToMatchUpdatableProperties(Novel expectedNovel) {
        assertNovelAllUpdatablePropertiesEquals(expectedNovel, getPersistedNovel(expectedNovel));
    }
}
