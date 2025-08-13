package com.shanzha.web.rest;

import static com.shanzha.domain.UserFootAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.UserFoot;
import com.shanzha.repository.UserFootRepository;
import com.shanzha.service.dto.UserFootDTO;
import com.shanzha.service.mapper.UserFootMapper;
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
 * Integration tests for the {@link UserFootResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserFootResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_DOCUMENT_ID = 1L;
    private static final Long UPDATED_DOCUMENT_ID = 2L;

    private static final Long DEFAULT_DOCUMENT_USER_ID = 1L;
    private static final Long UPDATED_DOCUMENT_USER_ID = 2L;

    private static final Integer DEFAULT_DOCUMENT_TYPE = 1;
    private static final Integer UPDATED_DOCUMENT_TYPE = 2;

    private static final Integer DEFAULT_READ_STAT = 1;
    private static final Integer UPDATED_READ_STAT = 2;

    private static final Integer DEFAULT_PRAISE_STAT = 1;
    private static final Integer UPDATED_PRAISE_STAT = 2;

    private static final Integer DEFAULT_COLLECTION_STAT = 1;
    private static final Integer UPDATED_COLLECTION_STAT = 2;

    private static final Integer DEFAULT_COMMENT_STAT = 1;
    private static final Integer UPDATED_COMMENT_STAT = 2;

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-foots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserFootRepository userFootRepository;

    @Autowired
    private UserFootMapper userFootMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserFootMockMvc;

    private UserFoot userFoot;

    private UserFoot insertedUserFoot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserFoot createEntity() {
        return new UserFoot()
            .userId(DEFAULT_USER_ID)
            .documentId(DEFAULT_DOCUMENT_ID)
            .documentUserId(DEFAULT_DOCUMENT_USER_ID)
            .documentType(DEFAULT_DOCUMENT_TYPE)
            .readStat(DEFAULT_READ_STAT)
            .praiseStat(DEFAULT_PRAISE_STAT)
            .collectionStat(DEFAULT_COLLECTION_STAT)
            .commentStat(DEFAULT_COMMENT_STAT)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserFoot createUpdatedEntity() {
        return new UserFoot()
            .userId(UPDATED_USER_ID)
            .documentId(UPDATED_DOCUMENT_ID)
            .documentUserId(UPDATED_DOCUMENT_USER_ID)
            .documentType(UPDATED_DOCUMENT_TYPE)
            .readStat(UPDATED_READ_STAT)
            .praiseStat(UPDATED_PRAISE_STAT)
            .collectionStat(UPDATED_COLLECTION_STAT)
            .commentStat(UPDATED_COMMENT_STAT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
    }

    @BeforeEach
    void initTest() {
        userFoot = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserFoot != null) {
            userFootRepository.delete(insertedUserFoot);
            insertedUserFoot = null;
        }
    }

    @Test
    @Transactional
    void createUserFoot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);
        var returnedUserFootDTO = om.readValue(
            restUserFootMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserFootDTO.class
        );

        // Validate the UserFoot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserFoot = userFootMapper.toEntity(returnedUserFootDTO);
        assertUserFootUpdatableFieldsEquals(returnedUserFoot, getPersistedUserFoot(returnedUserFoot));

        insertedUserFoot = returnedUserFoot;
    }

    @Test
    @Transactional
    void createUserFootWithExistingId() throws Exception {
        // Create the UserFoot with an existing ID
        userFoot.setId(1L);
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserFootMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userFoot.setUserId(null);

        // Create the UserFoot, which fails.
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        restUserFootMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocumentIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userFoot.setDocumentId(null);

        // Create the UserFoot, which fails.
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        restUserFootMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDocumentTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userFoot.setDocumentType(null);

        // Create the UserFoot, which fails.
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        restUserFootMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserFoots() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        // Get all the userFootList
        restUserFootMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userFoot.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].documentId").value(hasItem(DEFAULT_DOCUMENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].documentUserId").value(hasItem(DEFAULT_DOCUMENT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].documentType").value(hasItem(DEFAULT_DOCUMENT_TYPE)))
            .andExpect(jsonPath("$.[*].readStat").value(hasItem(DEFAULT_READ_STAT)))
            .andExpect(jsonPath("$.[*].praiseStat").value(hasItem(DEFAULT_PRAISE_STAT)))
            .andExpect(jsonPath("$.[*].collectionStat").value(hasItem(DEFAULT_COLLECTION_STAT)))
            .andExpect(jsonPath("$.[*].commentStat").value(hasItem(DEFAULT_COMMENT_STAT)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())));
    }

    @Test
    @Transactional
    void getUserFoot() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        // Get the userFoot
        restUserFootMockMvc
            .perform(get(ENTITY_API_URL_ID, userFoot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userFoot.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.documentId").value(DEFAULT_DOCUMENT_ID.intValue()))
            .andExpect(jsonPath("$.documentUserId").value(DEFAULT_DOCUMENT_USER_ID.intValue()))
            .andExpect(jsonPath("$.documentType").value(DEFAULT_DOCUMENT_TYPE))
            .andExpect(jsonPath("$.readStat").value(DEFAULT_READ_STAT))
            .andExpect(jsonPath("$.praiseStat").value(DEFAULT_PRAISE_STAT))
            .andExpect(jsonPath("$.collectionStat").value(DEFAULT_COLLECTION_STAT))
            .andExpect(jsonPath("$.commentStat").value(DEFAULT_COMMENT_STAT))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserFoot() throws Exception {
        // Get the userFoot
        restUserFootMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserFoot() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userFoot
        UserFoot updatedUserFoot = userFootRepository.findById(userFoot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserFoot are not directly saved in db
        em.detach(updatedUserFoot);
        updatedUserFoot
            .userId(UPDATED_USER_ID)
            .documentId(UPDATED_DOCUMENT_ID)
            .documentUserId(UPDATED_DOCUMENT_USER_ID)
            .documentType(UPDATED_DOCUMENT_TYPE)
            .readStat(UPDATED_READ_STAT)
            .praiseStat(UPDATED_PRAISE_STAT)
            .collectionStat(UPDATED_COLLECTION_STAT)
            .commentStat(UPDATED_COMMENT_STAT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);
        UserFootDTO userFootDTO = userFootMapper.toDto(updatedUserFoot);

        restUserFootMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userFootDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userFootDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserFootToMatchAllProperties(updatedUserFoot);
    }

    @Test
    @Transactional
    void putNonExistingUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userFootDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userFootDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userFootDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserFootWithPatch() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userFoot using partial update
        UserFoot partialUpdatedUserFoot = new UserFoot();
        partialUpdatedUserFoot.setId(userFoot.getId());

        partialUpdatedUserFoot
            .readStat(UPDATED_READ_STAT)
            .praiseStat(UPDATED_PRAISE_STAT)
            .commentStat(UPDATED_COMMENT_STAT)
            .updateTime(UPDATED_UPDATE_TIME);

        restUserFootMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserFoot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserFoot))
            )
            .andExpect(status().isOk());

        // Validate the UserFoot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserFootUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserFoot, userFoot), getPersistedUserFoot(userFoot));
    }

    @Test
    @Transactional
    void fullUpdateUserFootWithPatch() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userFoot using partial update
        UserFoot partialUpdatedUserFoot = new UserFoot();
        partialUpdatedUserFoot.setId(userFoot.getId());

        partialUpdatedUserFoot
            .userId(UPDATED_USER_ID)
            .documentId(UPDATED_DOCUMENT_ID)
            .documentUserId(UPDATED_DOCUMENT_USER_ID)
            .documentType(UPDATED_DOCUMENT_TYPE)
            .readStat(UPDATED_READ_STAT)
            .praiseStat(UPDATED_PRAISE_STAT)
            .collectionStat(UPDATED_COLLECTION_STAT)
            .commentStat(UPDATED_COMMENT_STAT)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME);

        restUserFootMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserFoot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserFoot))
            )
            .andExpect(status().isOk());

        // Validate the UserFoot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserFootUpdatableFieldsEquals(partialUpdatedUserFoot, getPersistedUserFoot(partialUpdatedUserFoot));
    }

    @Test
    @Transactional
    void patchNonExistingUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userFootDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userFootDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userFootDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserFoot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userFoot.setId(longCount.incrementAndGet());

        // Create the UserFoot
        UserFootDTO userFootDTO = userFootMapper.toDto(userFoot);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserFootMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userFootDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserFoot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserFoot() throws Exception {
        // Initialize the database
        insertedUserFoot = userFootRepository.saveAndFlush(userFoot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userFoot
        restUserFootMockMvc
            .perform(delete(ENTITY_API_URL_ID, userFoot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userFootRepository.count();
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

    protected UserFoot getPersistedUserFoot(UserFoot userFoot) {
        return userFootRepository.findById(userFoot.getId()).orElseThrow();
    }

    protected void assertPersistedUserFootToMatchAllProperties(UserFoot expectedUserFoot) {
        assertUserFootAllPropertiesEquals(expectedUserFoot, getPersistedUserFoot(expectedUserFoot));
    }

    protected void assertPersistedUserFootToMatchUpdatableProperties(UserFoot expectedUserFoot) {
        assertUserFootAllUpdatablePropertiesEquals(expectedUserFoot, getPersistedUserFoot(expectedUserFoot));
    }
}
