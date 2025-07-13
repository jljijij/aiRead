package com.shanzha.web.rest;

import static com.shanzha.domain.UserAiHistoryAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.UserAiHistory;
import com.shanzha.repository.UserAiHistoryRepository;
import com.shanzha.service.dto.UserAiHistoryDTO;
import com.shanzha.service.mapper.UserAiHistoryMapper;
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
 * Integration tests for the {@link UserAiHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserAiHistoryResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_QUESTION = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final String DEFAULT_CHAT_ID = "AAAAAAAAAA";
    private static final String UPDATED_CHAT_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-ai-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserAiHistoryRepository userAiHistoryRepository;

    @Autowired
    private UserAiHistoryMapper userAiHistoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserAiHistoryMockMvc;

    private UserAiHistory userAiHistory;

    private UserAiHistory insertedUserAiHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAiHistory createEntity() {
        return new UserAiHistory().userId(DEFAULT_USER_ID).question(DEFAULT_QUESTION).answer(DEFAULT_ANSWER).chatId(DEFAULT_CHAT_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAiHistory createUpdatedEntity() {
        return new UserAiHistory().userId(UPDATED_USER_ID).question(UPDATED_QUESTION).answer(UPDATED_ANSWER).chatId(UPDATED_CHAT_ID);
    }

    @BeforeEach
    void initTest() {
        userAiHistory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserAiHistory != null) {
            userAiHistoryRepository.delete(insertedUserAiHistory);
            insertedUserAiHistory = null;
        }
    }

    @Test
    @Transactional
    void createUserAiHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);
        var returnedUserAiHistoryDTO = om.readValue(
            restUserAiHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserAiHistoryDTO.class
        );

        // Validate the UserAiHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserAiHistory = userAiHistoryMapper.toEntity(returnedUserAiHistoryDTO);
        assertUserAiHistoryUpdatableFieldsEquals(returnedUserAiHistory, getPersistedUserAiHistory(returnedUserAiHistory));

        insertedUserAiHistory = returnedUserAiHistory;
    }

    @Test
    @Transactional
    void createUserAiHistoryWithExistingId() throws Exception {
        // Create the UserAiHistory with an existing ID
        userAiHistory.setId(1L);
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserAiHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAiHistory.setUserId(null);

        // Create the UserAiHistory, which fails.
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        restUserAiHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuestionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAiHistory.setQuestion(null);

        // Create the UserAiHistory, which fails.
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        restUserAiHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAnswerIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAiHistory.setAnswer(null);

        // Create the UserAiHistory, which fails.
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        restUserAiHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChatIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userAiHistory.setChatId(null);

        // Create the UserAiHistory, which fails.
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        restUserAiHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserAiHistories() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        // Get all the userAiHistoryList
        restUserAiHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userAiHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION)))
            .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER)))
            .andExpect(jsonPath("$.[*].chatId").value(hasItem(DEFAULT_CHAT_ID)));
    }

    @Test
    @Transactional
    void getUserAiHistory() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        // Get the userAiHistory
        restUserAiHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, userAiHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userAiHistory.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER))
            .andExpect(jsonPath("$.chatId").value(DEFAULT_CHAT_ID));
    }

    @Test
    @Transactional
    void getNonExistingUserAiHistory() throws Exception {
        // Get the userAiHistory
        restUserAiHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserAiHistory() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAiHistory
        UserAiHistory updatedUserAiHistory = userAiHistoryRepository.findById(userAiHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserAiHistory are not directly saved in db
        em.detach(updatedUserAiHistory);
        updatedUserAiHistory.userId(UPDATED_USER_ID).question(UPDATED_QUESTION).answer(UPDATED_ANSWER).chatId(UPDATED_CHAT_ID);
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(updatedUserAiHistory);

        restUserAiHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAiHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAiHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserAiHistoryToMatchAllProperties(updatedUserAiHistory);
    }

    @Test
    @Transactional
    void putNonExistingUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userAiHistoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAiHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userAiHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserAiHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAiHistory using partial update
        UserAiHistory partialUpdatedUserAiHistory = new UserAiHistory();
        partialUpdatedUserAiHistory.setId(userAiHistory.getId());

        partialUpdatedUserAiHistory.answer(UPDATED_ANSWER).chatId(UPDATED_CHAT_ID);

        restUserAiHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAiHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAiHistory))
            )
            .andExpect(status().isOk());

        // Validate the UserAiHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAiHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserAiHistory, userAiHistory),
            getPersistedUserAiHistory(userAiHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserAiHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userAiHistory using partial update
        UserAiHistory partialUpdatedUserAiHistory = new UserAiHistory();
        partialUpdatedUserAiHistory.setId(userAiHistory.getId());

        partialUpdatedUserAiHistory.userId(UPDATED_USER_ID).question(UPDATED_QUESTION).answer(UPDATED_ANSWER).chatId(UPDATED_CHAT_ID);

        restUserAiHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserAiHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserAiHistory))
            )
            .andExpect(status().isOk());

        // Validate the UserAiHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserAiHistoryUpdatableFieldsEquals(partialUpdatedUserAiHistory, getPersistedUserAiHistory(partialUpdatedUserAiHistory));
    }

    @Test
    @Transactional
    void patchNonExistingUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userAiHistoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAiHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userAiHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserAiHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userAiHistory.setId(longCount.incrementAndGet());

        // Create the UserAiHistory
        UserAiHistoryDTO userAiHistoryDTO = userAiHistoryMapper.toDto(userAiHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserAiHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userAiHistoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserAiHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserAiHistory() throws Exception {
        // Initialize the database
        insertedUserAiHistory = userAiHistoryRepository.saveAndFlush(userAiHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userAiHistory
        restUserAiHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, userAiHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userAiHistoryRepository.count();
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

    protected UserAiHistory getPersistedUserAiHistory(UserAiHistory userAiHistory) {
        return userAiHistoryRepository.findById(userAiHistory.getId()).orElseThrow();
    }

    protected void assertPersistedUserAiHistoryToMatchAllProperties(UserAiHistory expectedUserAiHistory) {
        assertUserAiHistoryAllPropertiesEquals(expectedUserAiHistory, getPersistedUserAiHistory(expectedUserAiHistory));
    }

    protected void assertPersistedUserAiHistoryToMatchUpdatableProperties(UserAiHistory expectedUserAiHistory) {
        assertUserAiHistoryAllUpdatablePropertiesEquals(expectedUserAiHistory, getPersistedUserAiHistory(expectedUserAiHistory));
    }
}
