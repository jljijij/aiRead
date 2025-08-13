const NovelUpload = () => import('@/novel/novel-upload.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/novel',
    name: 'NovelUpload',
    component: NovelUpload,
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
