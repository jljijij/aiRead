import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ChapterService from './chapter.service';
import { useDateFormat } from '@/shared/composables';
import { type IChapter } from '@/shared/model/chapter.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChapterDetails',
  setup() {
    const dateFormat = useDateFormat();
    const chapterService = inject('chapterService', () => new ChapterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const chapter: Ref<IChapter> = ref({});

    const retrieveChapter = async chapterId => {
      try {
        const res = await chapterService().find(chapterId);
        chapter.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.chapterId) {
      retrieveChapter(route.params.chapterId);
    }

    return {
      ...dateFormat,
      alertService,
      chapter,

      previousState,
    };
  },
});
