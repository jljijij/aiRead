import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import NovelService from './novel.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type INovel, Novel } from '@/shared/model/novel.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NovelUpdate',
  setup() {
    const novelService = inject('novelService', () => new NovelService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const novel: Ref<INovel> = ref(new Novel());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveNovel = async novelId => {
      try {
        const res = await novelService().find(novelId);
        res.createTime = new Date(res.createTime);
        res.updateTime = new Date(res.updateTime);
        novel.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.novelId) {
      retrieveNovel(route.params.novelId);
    }

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('本字段不能为空.'),
      },
      authorId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      coverUrl: {},
      description: {},
      categoryId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      tags: {},
      wordCount: {},
      chapterCount: {},
      status: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      isVip: {},
      createTime: {
        required: validations.required('本字段不能为空.'),
      },
      updateTime: {
        required: validations.required('本字段不能为空.'),
      },
    };
    const v$ = useVuelidate(validationRules, novel as any);
    v$.value.$validate();

    return {
      novelService,
      alertService,
      novel,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: novel }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.novel.id) {
        this.novelService()
          .update(this.novel)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Novel is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.novelService()
          .create(this.novel)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Novel is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
