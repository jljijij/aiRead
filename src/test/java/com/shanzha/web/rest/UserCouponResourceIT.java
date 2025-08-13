package com.shanzha.web.rest;

import static com.shanzha.domain.UserCouponAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.UserCoupon;
import com.shanzha.repository.UserCouponRepository;
import com.shanzha.service.dto.UserCouponDTO;
import com.shanzha.service.mapper.UserCouponMapper;
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
 * Integration tests for the {@link UserCouponResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserCouponResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_COUPON_ID = 1L;
    private static final Long UPDATED_COUPON_ID = 2L;

    private static final Instant DEFAULT_USED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_USED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserCouponMockMvc;

    private UserCoupon userCoupon;

    private UserCoupon insertedUserCoupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCoupon createEntity() {
        return new UserCoupon().userId(DEFAULT_USER_ID).couponId(DEFAULT_COUPON_ID).usedAt(DEFAULT_USED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserCoupon createUpdatedEntity() {
        return new UserCoupon().userId(UPDATED_USER_ID).couponId(UPDATED_COUPON_ID).usedAt(UPDATED_USED_AT);
    }

    @BeforeEach
    void initTest() {
        userCoupon = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUserCoupon != null) {
            userCouponRepository.delete(insertedUserCoupon);
            insertedUserCoupon = null;
        }
    }

    @Test
    @Transactional
    void createUserCoupon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);
        var returnedUserCouponDTO = om.readValue(
            restUserCouponMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userCouponDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UserCouponDTO.class
        );

        // Validate the UserCoupon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserCoupon = userCouponMapper.toEntity(returnedUserCouponDTO);
        assertUserCouponUpdatableFieldsEquals(returnedUserCoupon, getPersistedUserCoupon(returnedUserCoupon));

        insertedUserCoupon = returnedUserCoupon;
    }

    @Test
    @Transactional
    void createUserCouponWithExistingId() throws Exception {
        // Create the UserCoupon with an existing ID
        userCoupon.setId(1L);
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userCouponDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userCoupon.setUserId(null);

        // Create the UserCoupon, which fails.
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        restUserCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userCouponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCouponIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userCoupon.setCouponId(null);

        // Create the UserCoupon, which fails.
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        restUserCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userCouponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUserCoupons() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        // Get all the userCouponList
        restUserCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userCoupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].couponId").value(hasItem(DEFAULT_COUPON_ID.intValue())))
            .andExpect(jsonPath("$.[*].usedAt").value(hasItem(DEFAULT_USED_AT.toString())));
    }

    @Test
    @Transactional
    void getUserCoupon() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        // Get the userCoupon
        restUserCouponMockMvc
            .perform(get(ENTITY_API_URL_ID, userCoupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userCoupon.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.couponId").value(DEFAULT_COUPON_ID.intValue()))
            .andExpect(jsonPath("$.usedAt").value(DEFAULT_USED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUserCoupon() throws Exception {
        // Get the userCoupon
        restUserCouponMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUserCoupon() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userCoupon
        UserCoupon updatedUserCoupon = userCouponRepository.findById(userCoupon.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUserCoupon are not directly saved in db
        em.detach(updatedUserCoupon);
        updatedUserCoupon.userId(UPDATED_USER_ID).couponId(UPDATED_COUPON_ID).usedAt(UPDATED_USED_AT);
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(updatedUserCoupon);

        restUserCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userCouponDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userCouponDTO))
            )
            .andExpect(status().isOk());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserCouponToMatchAllProperties(updatedUserCoupon);
    }

    @Test
    @Transactional
    void putNonExistingUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userCouponDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userCouponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(userCouponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(userCouponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserCouponWithPatch() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userCoupon using partial update
        UserCoupon partialUpdatedUserCoupon = new UserCoupon();
        partialUpdatedUserCoupon.setId(userCoupon.getId());

        partialUpdatedUserCoupon.userId(UPDATED_USER_ID).usedAt(UPDATED_USED_AT);

        restUserCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserCoupon))
            )
            .andExpect(status().isOk());

        // Validate the UserCoupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserCouponUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserCoupon, userCoupon),
            getPersistedUserCoupon(userCoupon)
        );
    }

    @Test
    @Transactional
    void fullUpdateUserCouponWithPatch() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userCoupon using partial update
        UserCoupon partialUpdatedUserCoupon = new UserCoupon();
        partialUpdatedUserCoupon.setId(userCoupon.getId());

        partialUpdatedUserCoupon.userId(UPDATED_USER_ID).couponId(UPDATED_COUPON_ID).usedAt(UPDATED_USED_AT);

        restUserCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUserCoupon))
            )
            .andExpect(status().isOk());

        // Validate the UserCoupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserCouponUpdatableFieldsEquals(partialUpdatedUserCoupon, getPersistedUserCoupon(partialUpdatedUserCoupon));
    }

    @Test
    @Transactional
    void patchNonExistingUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userCouponDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userCouponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(userCouponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userCoupon.setId(longCount.incrementAndGet());

        // Create the UserCoupon
        UserCouponDTO userCouponDTO = userCouponMapper.toDto(userCoupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserCouponMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(userCouponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserCoupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserCoupon() throws Exception {
        // Initialize the database
        insertedUserCoupon = userCouponRepository.saveAndFlush(userCoupon);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userCoupon
        restUserCouponMockMvc
            .perform(delete(ENTITY_API_URL_ID, userCoupon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userCouponRepository.count();
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

    protected UserCoupon getPersistedUserCoupon(UserCoupon userCoupon) {
        return userCouponRepository.findById(userCoupon.getId()).orElseThrow();
    }

    protected void assertPersistedUserCouponToMatchAllProperties(UserCoupon expectedUserCoupon) {
        assertUserCouponAllPropertiesEquals(expectedUserCoupon, getPersistedUserCoupon(expectedUserCoupon));
    }

    protected void assertPersistedUserCouponToMatchUpdatableProperties(UserCoupon expectedUserCoupon) {
        assertUserCouponAllUpdatablePropertiesEquals(expectedUserCoupon, getPersistedUserCoupon(expectedUserCoupon));
    }
}
