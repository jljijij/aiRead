import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import NovelTagDetails from './novel-tag-details.vue';
import NovelTagService from './novel-tag.service';
import AlertService from '@/shared/alert/alert.service';

type NovelTagDetailsComponentType = InstanceType<typeof NovelTagDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const novelTagSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('NovelTag Management Detail Component', () => {
    let novelTagServiceStub: SinonStubbedInstance<NovelTagService>;
    let mountOptions: MountingOptions<NovelTagDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      novelTagServiceStub = sinon.createStubInstance<NovelTagService>(NovelTagService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          novelTagService: () => novelTagServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        novelTagServiceStub.find.resolves(novelTagSample);
        route = {
          params: {
            novelTagId: `${123}`,
          },
        };
        const wrapper = shallowMount(NovelTagDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.novelTag).toMatchObject(novelTagSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        novelTagServiceStub.find.resolves(novelTagSample);
        const wrapper = shallowMount(NovelTagDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
