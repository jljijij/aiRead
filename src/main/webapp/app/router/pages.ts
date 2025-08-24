const NovelUpload = () => import('@/novel/novel-upload.vue');
const CouponList = () => import('@/coupon/coupon-list.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/novel',
    name: 'NovelUpload',
    component: NovelUpload,
  },
  { path: '/coupons', name: 'CouponList', component: CouponList },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
