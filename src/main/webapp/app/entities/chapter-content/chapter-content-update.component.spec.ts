import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ChapterContentUpdate from './chapter-content-update.vue';
import ChapterContentService from './chapter-content.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import ChapterService from '@/entities/chapter/chapter.service';

type ChapterContentUpdateComponentType = InstanceType<typeof ChapterContentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const chapterContentSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ChapterContentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ChapterContent Management Update Component', () => {
    let comp: ChapterContentUpdateComponentType;
    let chapterContentServiceStub: SinonStubbedInstance<ChapterContentService>;

    beforeEach(() => {
      route = {};
      chapterContentServiceStub = sinon.createStubInstance<ChapterContentService>(ChapterContentService);
      chapterContentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          chapterContentService: () => chapterContentServiceStub,
          chapterService: () =>
            sinon.createStubInstance<ChapterService>(ChapterService, {
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
        const wrapper = shallowMount(ChapterContentUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ChapterContentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.chapterContent = chapterContentSample;
        chapterContentServiceStub.update.resolves(chapterContentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(chapterContentServiceStub.update.calledWith(chapterContentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        chapterContentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ChapterContentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.chapterContent = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(chapterContentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        chapterContentServiceStub.find.resolves(chapterContentSample);
        chapterContentServiceStub.retrieve.resolves([chapterContentSample]);

        // WHEN
        route = {
          params: {
            chapterContentId: `${chapterContentSample.id}`,
          },
        };
        const wrapper = shallowMount(ChapterContentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.chapterContent).toMatchObject(chapterContentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        chapterContentServiceStub.find.resolves(chapterContentSample);
        const wrapper = shallowMount(ChapterContentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
