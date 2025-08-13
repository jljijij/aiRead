import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ChapterService from './chapter.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import NovelService from '@/entities/novel/novel.service';
import { type INovel } from '@/shared/model/novel.model';
import { Chapter, type IChapter } from '@/shared/model/chapter.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChapterUpdate',
  setup() {
    const chapterService = inject('chapterService', () => new ChapterService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const chapter: Ref<IChapter> = ref(new Chapter());

    const novelService = inject('novelService', () => new NovelService());

    const novels: Ref<INovel[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveChapter = async chapterId => {
      try {
        const res = await chapterService().find(chapterId);
        res.createTime = new Date(res.createTime);
        res.updateTime = new Date(res.updateTime);
        chapter.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.chapterId) {
      retrieveChapter(route.params.chapterId);
    }

    const initRelationships = () => {
      novelService()
        .retrieve()
        .then(res => {
          novels.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      novelId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      chapterNo: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      title: {
        required: validations.required('本字段不能为空.'),
      },
      contentId: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      wordCount: {},
      isVip: {},
      price: {},
      createTime: {
        required: validations.required('本字段不能为空.'),
      },
      updateTime: {
        required: validations.required('本字段不能为空.'),
      },
      novel: {},
    };
    const v$ = useVuelidate(validationRules, chapter as any);
    v$.value.$validate();

    return {
      chapterService,
      alertService,
      chapter,
      previousState,
      isSaving,
      currentLanguage,
      novels,
      v$,
      ...useDateFormat({ entityRef: chapter }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.chapter.id) {
        this.chapterService()
          .update(this.chapter)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Chapter is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.chapterService()
          .create(this.chapter)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Chapter is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
