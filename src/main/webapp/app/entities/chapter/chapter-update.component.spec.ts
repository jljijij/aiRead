import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ChapterUpdate from './chapter-update.vue';
import ChapterService from './chapter.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import NovelService from '@/entities/novel/novel.service';

type ChapterUpdateComponentType = InstanceType<typeof ChapterUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const chapterSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ChapterUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Chapter Management Update Component', () => {
    let comp: ChapterUpdateComponentType;
    let chapterServiceStub: SinonStubbedInstance<ChapterService>;

    beforeEach(() => {
      route = {};
      chapterServiceStub = sinon.createStubInstance<ChapterService>(ChapterService);
      chapterServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          chapterService: () => chapterServiceStub,
          novelService: () =>
            sinon.createStubInstance<NovelService>(NovelService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ChapterUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ChapterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.chapter = chapterSample;
        chapterServiceStub.update.resolves(chapterSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(chapterServiceStub.update.calledWith(chapterSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        chapterServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ChapterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.chapter = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(chapterServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        chapterServiceStub.find.resolves(chapterSample);
        chapterServiceStub.retrieve.resolves([chapterSample]);

        // WHEN
        route = {
          params: {
            chapterId: `${chapterSample.id}`,
          },
        };
        const wrapper = shallowMount(ChapterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.chapter).toMatchObject(chapterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        chapterServiceStub.find.resolves(chapterSample);
        const wrapper = shallowMount(ChapterUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
