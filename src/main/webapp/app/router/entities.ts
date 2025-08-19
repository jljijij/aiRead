const Entities = () => import('@/entities/entities.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here
const ReadingVoucher = () => import('@/entities/reading-voucher/reading-voucher.vue');

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'reading-voucher',
      name: 'ReadingVoucher',
      component: ReadingVoucher,
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
