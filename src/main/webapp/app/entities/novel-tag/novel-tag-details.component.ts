import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import NovelTagService from './novel-tag.service';
import { useDateFormat } from '@/shared/composables';
import { type INovelTag } from '@/shared/model/novel-tag.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NovelTagDetails',
  setup() {
    const dateFormat = useDateFormat();
    const novelTagService = inject('novelTagService', () => new NovelTagService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const novelTag: Ref<INovelTag> = ref({});

    const retrieveNovelTag = async novelTagId => {
      try {
        const res = await novelTagService().find(novelTagId);
        novelTag.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.novelTagId) {
      retrieveNovelTag(route.params.novelTagId);
    }

    return {
      ...dateFormat,
      alertService,
      novelTag,

      previousState,
    };
  },
});
