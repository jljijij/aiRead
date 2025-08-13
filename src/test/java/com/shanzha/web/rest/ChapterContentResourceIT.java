package com.shanzha.web.rest;

import static com.shanzha.domain.ChapterContentAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.ChapterContent;
import com.shanzha.repository.ChapterContentRepository;
import com.shanzha.service.ChapterContentService;
import com.shanzha.service.dto.ChapterContentDTO;
import com.shanzha.service.mapper.ChapterContentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
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
 * Integration tests for the {@link ChapterContentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChapterContentResourceIT {

    private static final Long DEFAULT_NOVEL_ID = 1L;
    private static final Long UPDATED_NOVEL_ID = 2L;

    private static final Long DEFAULT_PAGE_ID = 1L;
    private static final Long UPDATED_PAGE_ID = 2L;

    private static final byte[] DEFAULT_COMPRESSED = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COMPRESSED = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COMPRESSED_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COMPRESSED_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_COMPRESSION_TYPE = 1;
    private static final Integer UPDATED_COMPRESSION_TYPE = 2;

    private static final String DEFAULT_HASH = "AAAAAAAAAA";
    private static final String UPDATED_HASH = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/chapter-contents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChapterContentRepository chapterContentRepository;

    @Mock
    private ChapterContentRepository chapterContentRepositoryMock;

    @Autowired
    private ChapterContentMapper chapterContentMapper;

    @Mock
    private ChapterContentService chapterContentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChapterContentMockMvc;

    private ChapterContent chapterContent;

    private ChapterContent insertedChapterContent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterContent createEntity() {
        return new ChapterContent()
            .novelId(DEFAULT_NOVEL_ID)
            .pageId(DEFAULT_PAGE_ID)
            .compressed(DEFAULT_COMPRESSED)
            .compressedContentType(DEFAULT_COMPRESSED_CONTENT_TYPE)
            .compressionType(DEFAULT_COMPRESSION_TYPE)
            .hash(DEFAULT_HASH)
            .createTime(DEFAULT_CREATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChapterContent createUpdatedEntity() {
        return new ChapterContent()
            .novelId(UPDATED_NOVEL_ID)
            .pageId(UPDATED_PAGE_ID)
            .compressed(UPDATED_COMPRESSED)
            .compressedContentType(UPDATED_COMPRESSED_CONTENT_TYPE)
            .compressionType(UPDATED_COMPRESSION_TYPE)
            .hash(UPDATED_HASH)
            .createTime(UPDATED_CREATE_TIME);
    }

    @BeforeEach
    void initTest() {
        chapterContent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedChapterContent != null) {
            chapterContentRepository.delete(insertedChapterContent);
            insertedChapterContent = null;
        }
    }

    @Test
    @Transactional
    void createChapterContent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);
        var returnedChapterContentDTO = om.readValue(
            restChapterContentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChapterContentDTO.class
        );

        // Validate the ChapterContent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChapterContent = chapterContentMapper.toEntity(returnedChapterContentDTO);
        assertChapterContentUpdatableFieldsEquals(returnedChapterContent, getPersistedChapterContent(returnedChapterContent));

        insertedChapterContent = returnedChapterContent;
    }

    @Test
    @Transactional
    void createChapterContentWithExistingId() throws Exception {
        // Create the ChapterContent with an existing ID
        chapterContent.setId(1L);
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChapterContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNovelIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterContent.setNovelId(null);

        // Create the ChapterContent, which fails.
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        restChapterContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPageIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterContent.setPageId(null);

        // Create the ChapterContent, which fails.
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        restChapterContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chapterContent.setCreateTime(null);

        // Create the ChapterContent, which fails.
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        restChapterContentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChapterContents() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        // Get all the chapterContentList
        restChapterContentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chapterContent.getId().intValue())))
            .andExpect(jsonPath("$.[*].novelId").value(hasItem(DEFAULT_NOVEL_ID.intValue())))
            .andExpect(jsonPath("$.[*].pageId").value(hasItem(DEFAULT_PAGE_ID.intValue())))
            .andExpect(jsonPath("$.[*].compressedContentType").value(hasItem(DEFAULT_COMPRESSED_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].compressed").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COMPRESSED))))
            .andExpect(jsonPath("$.[*].compressionType").value(hasItem(DEFAULT_COMPRESSION_TYPE)))
            .andExpect(jsonPath("$.[*].hash").value(hasItem(DEFAULT_HASH)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChapterContentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(chapterContentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chapterContentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChapterContentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chapterContentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChapterContentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chapterContentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChapterContent() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        // Get the chapterContent
        restChapterContentMockMvc
            .perform(get(ENTITY_API_URL_ID, chapterContent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chapterContent.getId().intValue()))
            .andExpect(jsonPath("$.novelId").value(DEFAULT_NOVEL_ID.intValue()))
            .andExpect(jsonPath("$.pageId").value(DEFAULT_PAGE_ID.intValue()))
            .andExpect(jsonPath("$.compressedContentType").value(DEFAULT_COMPRESSED_CONTENT_TYPE))
            .andExpect(jsonPath("$.compressed").value(Base64.getEncoder().encodeToString(DEFAULT_COMPRESSED)))
            .andExpect(jsonPath("$.compressionType").value(DEFAULT_COMPRESSION_TYPE))
            .andExpect(jsonPath("$.hash").value(DEFAULT_HASH))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingChapterContent() throws Exception {
        // Get the chapterContent
        restChapterContentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChapterContent() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterContent
        ChapterContent updatedChapterContent = chapterContentRepository.findById(chapterContent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChapterContent are not directly saved in db
        em.detach(updatedChapterContent);
        updatedChapterContent
            .novelId(UPDATED_NOVEL_ID)
            .pageId(UPDATED_PAGE_ID)
            .compressed(UPDATED_COMPRESSED)
            .compressedContentType(UPDATED_COMPRESSED_CONTENT_TYPE)
            .compressionType(UPDATED_COMPRESSION_TYPE)
            .hash(UPDATED_HASH)
            .createTime(UPDATED_CREATE_TIME);
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(updatedChapterContent);

        restChapterContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterContentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterContentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChapterContentToMatchAllProperties(updatedChapterContent);
    }

    @Test
    @Transactional
    void putNonExistingChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, chapterContentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chapterContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChapterContentWithPatch() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterContent using partial update
        ChapterContent partialUpdatedChapterContent = new ChapterContent();
        partialUpdatedChapterContent.setId(chapterContent.getId());

        partialUpdatedChapterContent.novelId(UPDATED_NOVEL_ID).compressionType(UPDATED_COMPRESSION_TYPE);

        restChapterContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterContent))
            )
            .andExpect(status().isOk());

        // Validate the ChapterContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterContentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedChapterContent, chapterContent),
            getPersistedChapterContent(chapterContent)
        );
    }

    @Test
    @Transactional
    void fullUpdateChapterContentWithPatch() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chapterContent using partial update
        ChapterContent partialUpdatedChapterContent = new ChapterContent();
        partialUpdatedChapterContent.setId(chapterContent.getId());

        partialUpdatedChapterContent
            .novelId(UPDATED_NOVEL_ID)
            .pageId(UPDATED_PAGE_ID)
            .compressed(UPDATED_COMPRESSED)
            .compressedContentType(UPDATED_COMPRESSED_CONTENT_TYPE)
            .compressionType(UPDATED_COMPRESSION_TYPE)
            .hash(UPDATED_HASH)
            .createTime(UPDATED_CREATE_TIME);

        restChapterContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChapterContent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChapterContent))
            )
            .andExpect(status().isOk());

        // Validate the ChapterContent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChapterContentUpdatableFieldsEquals(partialUpdatedChapterContent, getPersistedChapterContent(partialUpdatedChapterContent));
    }

    @Test
    @Transactional
    void patchNonExistingChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chapterContentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chapterContentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChapterContent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chapterContent.setId(longCount.incrementAndGet());

        // Create the ChapterContent
        ChapterContentDTO chapterContentDTO = chapterContentMapper.toDto(chapterContent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChapterContentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chapterContentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ChapterContent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChapterContent() throws Exception {
        // Initialize the database
        insertedChapterContent = chapterContentRepository.saveAndFlush(chapterContent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chapterContent
        restChapterContentMockMvc
            .perform(delete(ENTITY_API_URL_ID, chapterContent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chapterContentRepository.count();
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

    protected ChapterContent getPersistedChapterContent(ChapterContent chapterContent) {
        return chapterContentRepository.findById(chapterContent.getId()).orElseThrow();
    }

    protected void assertPersistedChapterContentToMatchAllProperties(ChapterContent expectedChapterContent) {
        assertChapterContentAllPropertiesEquals(expectedChapterContent, getPersistedChapterContent(expectedChapterContent));
    }

    protected void assertPersistedChapterContentToMatchUpdatableProperties(ChapterContent expectedChapterContent) {
        assertChapterContentAllUpdatablePropertiesEquals(expectedChapterContent, getPersistedChapterContent(expectedChapterContent));
    }
}
