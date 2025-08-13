package com.shanzha.web.rest;

import static com.shanzha.domain.ChapterAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.Chapter;
import com.shanzha.repository.ChapterRepository;
import com.shanzha.service.ChapterService;
import com.shanzha.service.dto.ChapterDTO;
import com.shanzha.service.mapper.ChapterMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
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
 * Integration tests for the {@link ChapterResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChapterResourceIT {

    private static final Integer DEFAULT_CHAPTER_NO = 1;
    private static final Integer UPDATED_CHAPTER_NO = 2;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_CONTENT_ID = 1L;
    private static final Long UPDATED_CONTENT_ID = 2L;

    private static final Integer DEFAULT_WORD_COUNT = 1;
    private static final Integer UPDATED_WORD_COUNT = 2;

    private static final Boolean DEFAULT_IS_VIP = false;
    private static final Boolean UPDATED_IS_VIP = true;

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/chapters";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterRepository chapterRepository;

    @Mock
    private ChapterRepository chapterRepositoryMock;

    @Autowired
    private ChapterMapper chapterMapper;

    @Mock
    private ChapterService chapterServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterMockMvc;

    private Chapter chapter;

    private Chapter insertedChapter;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createEntity() {
        return new Chapter()
            .chapterNo(DEFAULT_CHAPTER_NO)
            .title(DEFAULT_TITLE)
            .contentId(DEFAULT_CONTENT_ID)
            .wordCount(DEFAULT_WORD_COUNT)
            .isVip(DEFAULT_IS_VIP)
            .price(DEFAULT_PRICE)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chapter createUpdatedEntity() {
        return new Chapter()
            .chapterNo(UPDATED_CHAPTER_NO)
            .title(UPDATED_TITLE)
            .contentId(UPDATED_CONTENT_ID)
            .wordCount(UPDATED_WORD_COUNT)
            .isVip(UPDATED_IS_VIP)
            .price(UPDATED_PRICE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
    }

    @BeforeEach
    void initTest() {
        chapter = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChapter != null) {
            chapterRepository.delete(insertedChapter);
            insertedChapter = null;
        }
    }

    @Test
    @Transactional
    void createChapter() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);
        var returnedChapterDTO = om.readValue(
            restChapterMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterDTO.class
        );

        // Validate the Chapter in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapter = chapterMapper.toEntity(returnedChapterDTO);
        assertChapterUpdatableFieldsEquals(returnedChapter, getPersistedChapter(returnedChapter));

        insertedChapter = returnedChapter;
    }

    @Test
    @Transactional
    void createChapterWithExistingId() throws Exception {
        // Create the Chapter with an existing ID
        chapter.setId(1L);
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkChapterNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setChapterNo(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setTitle(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setContentId(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setCreateTime(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapter.setUpdateTime(null);

        // Create the Chapter, which fails.
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        restChapterMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapters() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get all the chapterList
        restChapterMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapter.getId().intValue())))
            .andExpect(jsonPath("$.[*].chapterNo").value(hasItem(DEFAULT_CHAPTER_NO)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].contentId").value(hasItem(DEFAULT_CONTENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].wordCount").value(hasItem(DEFAULT_WORD_COUNT)))
            .andExpect(jsonPath("$.[*].isVip").value(hasItem(DEFAULT_IS_VIP)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chapterServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChaptersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chapterServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chapterRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        // Get the chapter
        restChapterMockMvc
            .perform(get(ENTITY_API_URL_ID, chapter.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapter.getId().intValue()))
            .andExpect(jsonPath("$.chapterNo").value(DEFAULT_CHAPTER_NO))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.contentId").value(DEFAULT_CONTENT_ID.intValue()))
            .andExpect(jsonPath("$.wordCount").value(DEFAULT_WORD_COUNT))
            .andExpect(jsonPath("$.isVip").value(DEFAULT_IS_VIP))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChapter() throws Exception {
        // Get the chapter
        restChapterMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter
        Chapter updatedChapter = chapterRepository.findById(chapter.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapter are not directly saved in db
        em.detach(updatedChapter);
        updatedChapter
            .chapterNo(UPDATED_CHAPTER_NO)
            .title(UPDATED_TITLE)
            .contentId(UPDATED_CONTENT_ID)
            .wordCount(UPDATED_WORD_COUNT)
            .isVip(UPDATED_IS_VIP)
            .price(UPDATED_PRICE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        ChapterDTO chapterDTO = chapterMapper.toDto(updatedChapter);

        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterToMatchAllProperties(updatedChapter);
    }

    @Test
    @Transactional
    void putNonExistingChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter
            .chapterNo(UPDATED_CHAPTER_NO)
            .title(UPDATED_TITLE)
            .isVip(UPDATED_IS_VIP)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChapter, chapter), getPersistedChapter(chapter));
    }

    @Test
    @Transactional
    void fullUpdateChapterWithPatch() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapter using partial update
        Chapter partialUpdatedChapter = new Chapter();
        partialUpdatedChapter.setId(chapter.getId());

        partialUpdatedChapter
            .chapterNo(UPDATED_CHAPTER_NO)
            .title(UPDATED_TITLE)
            .contentId(UPDATED_CONTENT_ID)
            .wordCount(UPDATED_WORD_COUNT)
            .isVip(UPDATED_IS_VIP)
            .price(UPDATED_PRICE)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);

        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapter.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapter))
            )
            .andExpect(status().isOk());

        // Validate the Chapter in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterUpdatableFieldsEquals(partialUpdatedChapter, getPersistedChapter(partialUpdatedChapter));
    }

    @Test
    @Transactional
    void patchNonExistingChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapter() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapter.setId(longCount.incrementAndGet());

        // Create the Chapter
        ChapterDTO chapterDTO = chapterMapper.toDto(chapter);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chapter in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapter() throws Exception {
        // Initialize the database
        insertedChapter = chapterRepository.saveAndFlush(chapter);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapter
        restChapterMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapter.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterRepository.count();
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

    protected Chapter getPersistedChapter(Chapter chapter) {
        return chapterRepository.findById(chapter.getId()).orElseThrow();
    }

    protected void assertPersistedChapterToMatchAllProperties(Chapter expectedChapter) {
        assertChapterAllPropertiesEquals(expectedChapter, getPersistedChapter(expectedChapter));
    }

    protected void assertPersistedChapterToMatchUpdatableProperties(Chapter expectedChapter) {
        assertChapterAllUpdatablePropertiesEquals(expectedChapter, getPersistedChapter(expectedChapter));
    }
}
