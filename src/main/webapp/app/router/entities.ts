import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Novel = () => import('@/entities/novel/novel.vue');
const NovelUpdate = () => import('@/entities/novel/novel-update.vue');
const NovelDetails = () => import('@/entities/novel/novel-details.vue');

const Chapter = () => import('@/entities/chapter/chapter.vue');
const ChapterUpdate = () => import('@/entities/chapter/chapter-update.vue');
const ChapterDetails = () => import('@/entities/chapter/chapter-details.vue');

const ChapterContent = () => import('@/entities/chapter-content/chapter-content.vue');
const ChapterContentUpdate = () => import('@/entities/chapter-content/chapter-content-update.vue');
const ChapterContentDetails = () => import('@/entities/chapter-content/chapter-content-details.vue');

const NovelTag = () => import('@/entities/novel-tag/novel-tag.vue');
const NovelTagUpdate = () => import('@/entities/novel-tag/novel-tag-update.vue');
const NovelTagDetails = () => import('@/entities/novel-tag/novel-tag-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'novel',
      name: 'Novel',
      component: Novel,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel/new',
      name: 'NovelCreate',
      component: NovelUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel/:novelId/edit',
      name: 'NovelEdit',
      component: NovelUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel/:novelId/view',
      name: 'NovelView',
      component: NovelDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter',
      name: 'Chapter',
      component: Chapter,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter/new',
      name: 'ChapterCreate',
      component: ChapterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter/:chapterId/edit',
      name: 'ChapterEdit',
      component: ChapterUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter/:chapterId/view',
      name: 'ChapterView',
      component: ChapterDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter-content',
      name: 'ChapterContent',
      component: ChapterContent,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter-content/new',
      name: 'ChapterContentCreate',
      component: ChapterContentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter-content/:chapterContentId/edit',
      name: 'ChapterContentEdit',
      component: ChapterContentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'chapter-content/:chapterContentId/view',
      name: 'ChapterContentView',
      component: ChapterContentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel-tag',
      name: 'NovelTag',
      component: NovelTag,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel-tag/new',
      name: 'NovelTagCreate',
      component: NovelTagUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel-tag/:novelTagId/edit',
      name: 'NovelTagEdit',
      component: NovelTagUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'novel-tag/:novelTagId/view',
      name: 'NovelTagView',
      component: NovelTagDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
