import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import NovelTagService from './novel-tag.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type INovelTag, NovelTag } from '@/shared/model/novel-tag.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NovelTagUpdate',
  setup() {
    const novelTagService = inject('novelTagService', () => new NovelTagService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const novelTag: Ref<INovelTag> = ref(new NovelTag());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveNovelTag = async novelTagId => {
      try {
        const res = await novelTagService().find(novelTagId);
        res.createTime = new Date(res.createTime);
        res.updateTime = new Date(res.updateTime);
        novelTag.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.novelTagId) {
      retrieveNovelTag(route.params.novelTagId);
    }

    const validations = useValidation();
    const validationRules = {
      tagId: {
        required: validations.required('本字段不能为空.'),
      },
      tagName: {
        required: validations.required('本字段不能为空.'),
      },
      category: {
        required: validations.required('本字段不能为空.'),
      },
      isHot: {},
      createTime: {
        required: validations.required('本字段不能为空.'),
      },
      updateTime: {
        required: validations.required('本字段不能为空.'),
      },
    };
    const v$ = useVuelidate(validationRules, novelTag as any);
    v$.value.$validate();

    return {
      novelTagService,
      alertService,
      novelTag,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: novelTag }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.novelTag.id) {
        this.novelTagService()
          .update(this.novelTag)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A NovelTag is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.novelTagService()
          .create(this.novelTag)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A NovelTag is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
