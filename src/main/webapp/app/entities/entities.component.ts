import { defineComponent, provide } from 'vue';

import NovelService from './novel/novel.service';
import ChapterService from './chapter/chapter.service';
import ChapterContentService from './chapter-content/chapter-content.service';
import NovelTagService from './novel-tag/novel-tag.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('novelService', () => new NovelService());
    provide('chapterService', () => new ChapterService());
    provide('chapterContentService', () => new ChapterContentService());
    provide('novelTagService', () => new NovelTagService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
