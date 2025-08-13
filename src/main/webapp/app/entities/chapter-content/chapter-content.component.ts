import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import ChapterContentService from './chapter-content.service';
import { type IChapterContent } from '@/shared/model/chapter-content.model';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChapterContent',
  setup() {
    const dateFormat = useDateFormat();
    const dataUtils = useDataUtils();
    const chapterContentService = inject('chapterContentService', () => new ChapterContentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const chapterContents: Ref<IChapterContent[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveChapterContents = async () => {
      isFetching.value = true;
      try {
        const res = await chapterContentService().retrieve();
        chapterContents.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveChapterContents();
    };

    onMounted(async () => {
      await retrieveChapterContents();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IChapterContent) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeChapterContent = async () => {
      try {
        await chapterContentService().delete(removeId.value);
        const message = `A ChapterContent is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveChapterContents();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      chapterContents,
      handleSyncList,
      isFetching,
      retrieveChapterContents,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeChapterContent,
      ...dataUtils,
    };
  },
});
