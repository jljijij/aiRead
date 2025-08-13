import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import NovelUpdate from './novel-update.vue';
import NovelService from './novel.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type NovelUpdateComponentType = InstanceType<typeof NovelUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const novelSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<NovelUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Novel Management Update Component', () => {
    let comp: NovelUpdateComponentType;
    let novelServiceStub: SinonStubbedInstance<NovelService>;

    beforeEach(() => {
      route = {};
      novelServiceStub = sinon.createStubInstance<NovelService>(NovelService);
      novelServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          novelService: () => novelServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(NovelUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(NovelUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.novel = novelSample;
        novelServiceStub.update.resolves(novelSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(novelServiceStub.update.calledWith(novelSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        novelServiceStub.create.resolves(entity);
        const wrapper = shallowMount(NovelUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.novel = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(novelServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        novelServiceStub.find.resolves(novelSample);
        novelServiceStub.retrieve.resolves([novelSample]);

        // WHEN
        route = {
          params: {
            novelId: `${novelSample.id}`,
          },
        };
        const wrapper = shallowMount(NovelUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.novel).toMatchObject(novelSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        novelServiceStub.find.resolves(novelSample);
        const wrapper = shallowMount(NovelUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
