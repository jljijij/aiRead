import { type Ref, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import ChapterContentService from './chapter-content.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IChapterContent } from '@/shared/model/chapter-content.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChapterContentDetails',
  setup() {
    const dateFormat = useDateFormat();
    const chapterContentService = inject('chapterContentService', () => new ChapterContentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const chapterContent: Ref<IChapterContent> = ref({});

    const retrieveChapterContent = async chapterContentId => {
      try {
        const res = await chapterContentService().find(chapterContentId);
        chapterContent.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.chapterContentId) {
      retrieveChapterContent(route.params.chapterContentId);
    }

    return {
      ...dateFormat,
      alertService,
      chapterContent,

      ...dataUtils,

      previousState,
    };
  },
});
