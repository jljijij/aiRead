package com.shanzha.web.rest;

import static com.shanzha.domain.NotifyMsgAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.NotifyMsg;
import com.shanzha.repository.NotifyMsgRepository;
import com.shanzha.service.dto.NotifyMsgDTO;
import com.shanzha.service.mapper.NotifyMsgMapper;
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
 * Integration tests for the {@link NotifyMsgResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotifyMsgResourceIT {

    private static final String DEFAULT_RELATED_ID = "AAAAAAAAAA";
    private static final String UPDATED_RELATED_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RELATED_INFO = "AAAAAAAAAA";
    private static final String UPDATED_RELATED_INFO = "BBBBBBBBBB";

    private static final Long DEFAULT_OPERATE_USER_ID = 1L;
    private static final Long UPDATED_OPERATE_USER_ID = 2L;

    private static final String DEFAULT_OPERATE_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OPERATE_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OPERATE_USER_PHOTO = "AAAAAAAAAA";
    private static final String UPDATED_OPERATE_USER_PHOTO = "BBBBBBBBBB";

    private static final Integer DEFAULT_TYPE = 1;
    private static final Integer UPDATED_TYPE = 2;

    private static final String DEFAULT_MSG = "AAAAAAAAAA";
    private static final String UPDATED_MSG = "BBBBBBBBBB";

    private static final Integer DEFAULT_STATE = 1;
    private static final Integer UPDATED_STATE = 2;

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notify-msgs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotifyMsgRepository notifyMsgRepository;

    @Autowired
    private NotifyMsgMapper notifyMsgMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotifyMsgMockMvc;

    private NotifyMsg notifyMsg;

    private NotifyMsg insertedNotifyMsg;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotifyMsg createEntity() {
        return new NotifyMsg()
            .relatedId(DEFAULT_RELATED_ID)
            .relatedInfo(DEFAULT_RELATED_INFO)
            .operateUserId(DEFAULT_OPERATE_USER_ID)
            .operateUserName(DEFAULT_OPERATE_USER_NAME)
            .operateUserPhoto(DEFAULT_OPERATE_USER_PHOTO)
            .type(DEFAULT_TYPE)
            .msg(DEFAULT_MSG)
            .state(DEFAULT_STATE)
            .createTime(DEFAULT_CREATE_TIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotifyMsg createUpdatedEntity() {
        return new NotifyMsg()
            .relatedId(UPDATED_RELATED_ID)
            .relatedInfo(UPDATED_RELATED_INFO)
            .operateUserId(UPDATED_OPERATE_USER_ID)
            .operateUserName(UPDATED_OPERATE_USER_NAME)
            .operateUserPhoto(UPDATED_OPERATE_USER_PHOTO)
            .type(UPDATED_TYPE)
            .msg(UPDATED_MSG)
            .state(UPDATED_STATE)
            .createTime(UPDATED_CREATE_TIME);
    }

    @BeforeEach
    void initTest() {
        notifyMsg = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotifyMsg != null) {
            notifyMsgRepository.delete(insertedNotifyMsg);
            insertedNotifyMsg = null;
        }
    }

    @Test
    @Transactional
    void createNotifyMsg() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);
        var returnedNotifyMsgDTO = om.readValue(
            restNotifyMsgMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotifyMsgDTO.class
        );

        // Validate the NotifyMsg in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotifyMsg = notifyMsgMapper.toEntity(returnedNotifyMsgDTO);
        assertNotifyMsgUpdatableFieldsEquals(returnedNotifyMsg, getPersistedNotifyMsg(returnedNotifyMsg));

        insertedNotifyMsg = returnedNotifyMsg;
    }

    @Test
    @Transactional
    void createNotifyMsgWithExistingId() throws Exception {
        // Create the NotifyMsg with an existing ID
        notifyMsg.setId(1L);
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotifyMsgMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isBadRequest());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRelatedIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notifyMsg.setRelatedId(null);

        // Create the NotifyMsg, which fails.
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        restNotifyMsgMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperateUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notifyMsg.setOperateUserId(null);

        // Create the NotifyMsg, which fails.
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        restNotifyMsgMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notifyMsg.setType(null);

        // Create the NotifyMsg, which fails.
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        restNotifyMsgMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notifyMsg.setState(null);

        // Create the NotifyMsg, which fails.
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        restNotifyMsgMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotifyMsgs() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        // Get all the notifyMsgList
        restNotifyMsgMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notifyMsg.getId().intValue())))
            .andExpect(jsonPath("$.[*].relatedId").value(hasItem(DEFAULT_RELATED_ID)))
            .andExpect(jsonPath("$.[*].relatedInfo").value(hasItem(DEFAULT_RELATED_INFO)))
            .andExpect(jsonPath("$.[*].operateUserId").value(hasItem(DEFAULT_OPERATE_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].operateUserName").value(hasItem(DEFAULT_OPERATE_USER_NAME)))
            .andExpect(jsonPath("$.[*].operateUserPhoto").value(hasItem(DEFAULT_OPERATE_USER_PHOTO)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].msg").value(hasItem(DEFAULT_MSG)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())));
    }

    @Test
    @Transactional
    void getNotifyMsg() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        // Get the notifyMsg
        restNotifyMsgMockMvc
            .perform(get(ENTITY_API_URL_ID, notifyMsg.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notifyMsg.getId().intValue()))
            .andExpect(jsonPath("$.relatedId").value(DEFAULT_RELATED_ID))
            .andExpect(jsonPath("$.relatedInfo").value(DEFAULT_RELATED_INFO))
            .andExpect(jsonPath("$.operateUserId").value(DEFAULT_OPERATE_USER_ID.intValue()))
            .andExpect(jsonPath("$.operateUserName").value(DEFAULT_OPERATE_USER_NAME))
            .andExpect(jsonPath("$.operateUserPhoto").value(DEFAULT_OPERATE_USER_PHOTO))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.msg").value(DEFAULT_MSG))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()));
    }

    @Test
    @Transactional
    void getNonExistingNotifyMsg() throws Exception {
        // Get the notifyMsg
        restNotifyMsgMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotifyMsg() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notifyMsg
        NotifyMsg updatedNotifyMsg = notifyMsgRepository.findById(notifyMsg.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotifyMsg are not directly saved in db
        em.detach(updatedNotifyMsg);
        updatedNotifyMsg
            .relatedId(UPDATED_RELATED_ID)
            .relatedInfo(UPDATED_RELATED_INFO)
            .operateUserId(UPDATED_OPERATE_USER_ID)
            .operateUserName(UPDATED_OPERATE_USER_NAME)
            .operateUserPhoto(UPDATED_OPERATE_USER_PHOTO)
            .type(UPDATED_TYPE)
            .msg(UPDATED_MSG)
            .state(UPDATED_STATE)
            .createTime(UPDATED_CREATE_TIME);
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(updatedNotifyMsg);

        restNotifyMsgMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notifyMsgDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notifyMsgDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotifyMsgToMatchAllProperties(updatedNotifyMsg);
    }

    @Test
    @Transactional
    void putNonExistingNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notifyMsgDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notifyMsgDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notifyMsgDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotifyMsgWithPatch() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notifyMsg using partial update
        NotifyMsg partialUpdatedNotifyMsg = new NotifyMsg();
        partialUpdatedNotifyMsg.setId(notifyMsg.getId());

        partialUpdatedNotifyMsg.relatedId(UPDATED_RELATED_ID).relatedInfo(UPDATED_RELATED_INFO).operateUserId(UPDATED_OPERATE_USER_ID);

        restNotifyMsgMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotifyMsg.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotifyMsg))
            )
            .andExpect(status().isOk());

        // Validate the NotifyMsg in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotifyMsgUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotifyMsg, notifyMsg),
            getPersistedNotifyMsg(notifyMsg)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotifyMsgWithPatch() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notifyMsg using partial update
        NotifyMsg partialUpdatedNotifyMsg = new NotifyMsg();
        partialUpdatedNotifyMsg.setId(notifyMsg.getId());

        partialUpdatedNotifyMsg
            .relatedId(UPDATED_RELATED_ID)
            .relatedInfo(UPDATED_RELATED_INFO)
            .operateUserId(UPDATED_OPERATE_USER_ID)
            .operateUserName(UPDATED_OPERATE_USER_NAME)
            .operateUserPhoto(UPDATED_OPERATE_USER_PHOTO)
            .type(UPDATED_TYPE)
            .msg(UPDATED_MSG)
            .state(UPDATED_STATE)
            .createTime(UPDATED_CREATE_TIME);

        restNotifyMsgMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotifyMsg.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotifyMsg))
            )
            .andExpect(status().isOk());

        // Validate the NotifyMsg in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotifyMsgUpdatableFieldsEquals(partialUpdatedNotifyMsg, getPersistedNotifyMsg(partialUpdatedNotifyMsg));
    }

    @Test
    @Transactional
    void patchNonExistingNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notifyMsgDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notifyMsgDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notifyMsgDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotifyMsg() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notifyMsg.setId(longCount.incrementAndGet());

        // Create the NotifyMsg
        NotifyMsgDTO notifyMsgDTO = notifyMsgMapper.toDto(notifyMsg);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotifyMsgMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notifyMsgDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotifyMsg in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotifyMsg() throws Exception {
        // Initialize the database
        insertedNotifyMsg = notifyMsgRepository.saveAndFlush(notifyMsg);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notifyMsg
        restNotifyMsgMockMvc
            .perform(delete(ENTITY_API_URL_ID, notifyMsg.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notifyMsgRepository.count();
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

    protected NotifyMsg getPersistedNotifyMsg(NotifyMsg notifyMsg) {
        return notifyMsgRepository.findById(notifyMsg.getId()).orElseThrow();
    }

    protected void assertPersistedNotifyMsgToMatchAllProperties(NotifyMsg expectedNotifyMsg) {
        assertNotifyMsgAllPropertiesEquals(expectedNotifyMsg, getPersistedNotifyMsg(expectedNotifyMsg));
    }

    protected void assertPersistedNotifyMsgToMatchUpdatableProperties(NotifyMsg expectedNotifyMsg) {
        assertNotifyMsgAllUpdatablePropertiesEquals(expectedNotifyMsg, getPersistedNotifyMsg(expectedNotifyMsg));
    }
}
