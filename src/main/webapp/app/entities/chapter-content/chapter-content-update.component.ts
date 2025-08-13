import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ChapterContentService from './chapter-content.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import ChapterService from '@/entities/chapter/chapter.service';
import { type IChapter } from '@/shared/model/chapter.model';
import { ChapterContent, type IChapterContent } from '@/shared/model/chapter-content.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChapterContentUpdate',
  setup() {
    const chapterContentService = inject('chapterContentService', () => new ChapterContentService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const chapterContent: Ref<IChapterContent> = ref(new ChapterContent());

    const chapterService = inject('chapterService', () => new ChapterService());

    const chapters: Ref<IChapter[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveChapterContent = async chapterContentId => {
      try {
        const res = await chapterContentService().find(chapterContentId);
        res.createTime = new Date(res.createTime);
        chapterContent.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.chapterContentId) {
      retrieveChapterContent(route.params.chapterContentId);
    }

    const initRelationships = () => {
      chapterService()
        .retrieve()
        .then(res => {
          chapters.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();

    const validations = useValidation();
    const validationRules = {
      novelId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      chapterId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      pageId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      compressed: {
        required: validations.required('本字段不能为空.'),
      },
      compressionType: {},
      hash: {},
      createTime: {
        required: validations.required('本字段不能为空.'),
      },
      chapter: {},
    };
    const v$ = useVuelidate(validationRules, chapterContent as any);
    v$.value.$validate();

    return {
      chapterContentService,
      alertService,
      chapterContent,
      previousState,
      isSaving,
      currentLanguage,
      chapters,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: chapterContent }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.chapterContent.id) {
        this.chapterContentService()
          .update(this.chapterContent)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A ChapterContent is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.chapterContentService()
          .create(this.chapterContent)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A ChapterContent is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
