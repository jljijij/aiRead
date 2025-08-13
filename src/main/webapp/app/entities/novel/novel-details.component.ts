import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import NovelService from './novel.service';
import { useDateFormat } from '@/shared/composables';
import { type INovel } from '@/shared/model/novel.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NovelDetails',
  setup() {
    const dateFormat = useDateFormat();
    const novelService = inject('novelService', () => new NovelService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const novel: Ref<INovel> = ref({});

    const retrieveNovel = async novelId => {
      try {
        const res = await novelService().find(novelId);
        novel.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.novelId) {
      retrieveNovel(route.params.novelId);
    }

    return {
      ...dateFormat,
      alertService,
      novel,

      previousState,
    };
  },
});
