import { defineComponent, provide } from 'vue';

import UserService from '@/entities/user/user.service';
import ReadingVoucherService from '@/entities/reading-voucher/reading-voucher.service';
import CommentService from '@/entities/comment/comment.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('readingVoucherService', () => new ReadingVoucherService());
    provide('commentService', () => new CommentService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
