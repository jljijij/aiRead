import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import NovelTagUpdate from './novel-tag-update.vue';
import NovelTagService from './novel-tag.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type NovelTagUpdateComponentType = InstanceType<typeof NovelTagUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const novelTagSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<NovelTagUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('NovelTag Management Update Component', () => {
    let comp: NovelTagUpdateComponentType;
    let novelTagServiceStub: SinonStubbedInstance<NovelTagService>;

    beforeEach(() => {
      route = {};
      novelTagServiceStub = sinon.createStubInstance<NovelTagService>(NovelTagService);
      novelTagServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          novelTagService: () => novelTagServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(NovelTagUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(NovelTagUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.novelTag = novelTagSample;
        novelTagServiceStub.update.resolves(novelTagSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(novelTagServiceStub.update.calledWith(novelTagSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        novelTagServiceStub.create.resolves(entity);
        const wrapper = shallowMount(NovelTagUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.novelTag = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(novelTagServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        novelTagServiceStub.find.resolves(novelTagSample);
        novelTagServiceStub.retrieve.resolves([novelTagSample]);

        // WHEN
        route = {
          params: {
            novelTagId: `${novelTagSample.id}`,
          },
        };
        const wrapper = shallowMount(NovelTagUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.novelTag).toMatchObject(novelTagSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        novelTagServiceStub.find.resolves(novelTagSample);
        const wrapper = shallowMount(NovelTagUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
