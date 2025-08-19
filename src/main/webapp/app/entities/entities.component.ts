import { defineComponent, provide } from 'vue';

import UserService from '@/entities/user/user.service';
import ReadingVoucherService from '@/entities/reading-voucher/reading-voucher.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('readingVoucherService', () => new ReadingVoucherService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
