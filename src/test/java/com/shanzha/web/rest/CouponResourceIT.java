package com.shanzha.web.rest;

import static com.shanzha.domain.CouponAsserts.*;
import static com.shanzha.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanzha.IntegrationTest;
import com.shanzha.domain.Coupon;
import com.shanzha.domain.enumeration.CouponType;
import com.shanzha.repository.CouponRepository;
import com.shanzha.service.dto.CouponDTO;
import com.shanzha.service.mapper.CouponMapper;
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
 * Integration tests for the {@link CouponResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CouponResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final CouponType DEFAULT_TYPE = CouponType.FREE_CHAPTER;
    private static final CouponType UPDATED_TYPE = CouponType.FREE_PACKAGE;

    private static final Long DEFAULT_NOVEL_ID = 1L;
    private static final Long UPDATED_NOVEL_ID = 2L;

    private static final Boolean DEFAULT_USED = false;
    private static final Boolean UPDATED_USED = true;

    private static final Long DEFAULT_CHAPTER_ID = 1L;
    private static final Long UPDATED_CHAPTER_ID = 2L;

    private static final Long DEFAULT_PACKAGE_ID = 1L;
    private static final Long UPDATED_PACKAGE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/coupons";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCouponMockMvc;

    private Coupon coupon;

    private Coupon insertedCoupon;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createEntity() {
        return new Coupon()
            .code(DEFAULT_CODE)
            .expiredAt(DEFAULT_EXPIRED_AT)
            .type(DEFAULT_TYPE)
            .novelId(DEFAULT_NOVEL_ID)
            .used(DEFAULT_USED)
            .chapterId(DEFAULT_CHAPTER_ID)
            .packageId(DEFAULT_PACKAGE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coupon createUpdatedEntity() {
        return new Coupon()
            .code(UPDATED_CODE)
            .expiredAt(UPDATED_EXPIRED_AT)
            .type(UPDATED_TYPE)
            .novelId(UPDATED_NOVEL_ID)
            .used(UPDATED_USED)
            .chapterId(UPDATED_CHAPTER_ID)
            .packageId(UPDATED_PACKAGE_ID);
    }

    @BeforeEach
    void initTest() {
        coupon = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCoupon != null) {
            couponRepository.delete(insertedCoupon);
            insertedCoupon = null;
        }
    }

    @Test
    @Transactional
    void createCoupon() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);
        var returnedCouponDTO = om.readValue(
            restCouponMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CouponDTO.class
        );

        // Validate the Coupon in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCoupon = couponMapper.toEntity(returnedCouponDTO);
        assertCouponUpdatableFieldsEquals(returnedCoupon, getPersistedCoupon(returnedCoupon));

        insertedCoupon = returnedCoupon;
    }

    @Test
    @Transactional
    void createCouponWithExistingId() throws Exception {
        // Create the Coupon with an existing ID
        coupon.setId(1L);
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setCode(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiredAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setExpiredAt(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setType(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNovelIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        coupon.setNovelId(null);

        // Create the Coupon, which fails.
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        restCouponMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCoupons() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get all the couponList
        restCouponMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coupon.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].expiredAt").value(hasItem(DEFAULT_EXPIRED_AT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].novelId").value(hasItem(DEFAULT_NOVEL_ID.intValue())))
            .andExpect(jsonPath("$.[*].used").value(hasItem(DEFAULT_USED)))
            .andExpect(jsonPath("$.[*].chapterId").value(hasItem(DEFAULT_CHAPTER_ID.intValue())))
            .andExpect(jsonPath("$.[*].packageId").value(hasItem(DEFAULT_PACKAGE_ID.intValue())));
    }

    @Test
    @Transactional
    void getCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        // Get the coupon
        restCouponMockMvc
            .perform(get(ENTITY_API_URL_ID, coupon.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coupon.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.expiredAt").value(DEFAULT_EXPIRED_AT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.novelId").value(DEFAULT_NOVEL_ID.intValue()))
            .andExpect(jsonPath("$.used").value(DEFAULT_USED))
            .andExpect(jsonPath("$.chapterId").value(DEFAULT_CHAPTER_ID.intValue()))
            .andExpect(jsonPath("$.packageId").value(DEFAULT_PACKAGE_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingCoupon() throws Exception {
        // Get the coupon
        restCouponMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon
        Coupon updatedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCoupon are not directly saved in db
        em.detach(updatedCoupon);
        updatedCoupon
            .code(UPDATED_CODE)
            .expiredAt(UPDATED_EXPIRED_AT)
            .type(UPDATED_TYPE)
            .novelId(UPDATED_NOVEL_ID)
            .used(UPDATED_USED)
            .chapterId(UPDATED_CHAPTER_ID)
            .packageId(UPDATED_PACKAGE_ID);
        CouponDTO couponDTO = couponMapper.toDto(updatedCoupon);

        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCouponToMatchAllProperties(updatedCoupon);
    }

    @Test
    @Transactional
    void putNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, couponDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon
            .code(UPDATED_CODE)
            .expiredAt(UPDATED_EXPIRED_AT)
            .type(UPDATED_TYPE)
            .used(UPDATED_USED)
            .chapterId(UPDATED_CHAPTER_ID)
            .packageId(UPDATED_PACKAGE_ID);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCoupon, coupon), getPersistedCoupon(coupon));
    }

    @Test
    @Transactional
    void fullUpdateCouponWithPatch() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the coupon using partial update
        Coupon partialUpdatedCoupon = new Coupon();
        partialUpdatedCoupon.setId(coupon.getId());

        partialUpdatedCoupon
            .code(UPDATED_CODE)
            .expiredAt(UPDATED_EXPIRED_AT)
            .type(UPDATED_TYPE)
            .novelId(UPDATED_NOVEL_ID)
            .used(UPDATED_USED)
            .chapterId(UPDATED_CHAPTER_ID)
            .packageId(UPDATED_PACKAGE_ID);

        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCoupon.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCoupon))
            )
            .andExpect(status().isOk());

        // Validate the Coupon in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCouponUpdatableFieldsEquals(partialUpdatedCoupon, getPersistedCoupon(partialUpdatedCoupon));
    }

    @Test
    @Transactional
    void patchNonExistingCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, couponDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(couponDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCoupon() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        coupon.setId(longCount.incrementAndGet());

        // Create the Coupon
        CouponDTO couponDTO = couponMapper.toDto(coupon);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCouponMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(couponDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Coupon in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCoupon() throws Exception {
        // Initialize the database
        insertedCoupon = couponRepository.saveAndFlush(coupon);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the coupon
        restCouponMockMvc
            .perform(delete(ENTITY_API_URL_ID, coupon.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return couponRepository.count();
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

    protected Coupon getPersistedCoupon(Coupon coupon) {
        return couponRepository.findById(coupon.getId()).orElseThrow();
    }

    protected void assertPersistedCouponToMatchAllProperties(Coupon expectedCoupon) {
        assertCouponAllPropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }

    protected void assertPersistedCouponToMatchUpdatableProperties(Coupon expectedCoupon) {
        assertCouponAllUpdatablePropertiesEquals(expectedCoupon, getPersistedCoupon(expectedCoupon));
    }
}
