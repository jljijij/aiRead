import { type Ref, defineComponent, inject, onMounted, ref } from 'vue';

import NovelService from './novel.service';
import { type INovel } from '@/shared/model/novel.model';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Novel',
  setup() {
    const dateFormat = useDateFormat();
    const novelService = inject('novelService', () => new NovelService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const novels: Ref<INovel[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveNovels = async () => {
      isFetching.value = true;
      try {
        const res = await novelService().retrieve();
        novels.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveNovels();
    };

    onMounted(async () => {
      await retrieveNovels();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: INovel) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeNovel = async () => {
      try {
        await novelService().delete(removeId.value);
        const message = `A Novel is deleted with identifier ${removeId.value}`;
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveNovels();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      novels,
      handleSyncList,
      isFetching,
      retrieveNovels,
      clear,
      ...dateFormat,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeNovel,
    };
  },
});
