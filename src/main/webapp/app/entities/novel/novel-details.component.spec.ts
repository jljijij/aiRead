import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import NovelDetails from './novel-details.vue';
import NovelService from './novel.service';
import AlertService from '@/shared/alert/alert.service';

type NovelDetailsComponentType = InstanceType<typeof NovelDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const novelSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Novel Management Detail Component', () => {
    let novelServiceStub: SinonStubbedInstance<NovelService>;
    let mountOptions: MountingOptions<NovelDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      novelServiceStub = sinon.createStubInstance<NovelService>(NovelService);

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
          novelService: () => novelServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        novelServiceStub.find.resolves(novelSample);
        route = {
          params: {
            novelId: `${123}`,
          },
        };
        const wrapper = shallowMount(NovelDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.novel).toMatchObject(novelSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        novelServiceStub.find.resolves(novelSample);
        const wrapper = shallowMount(NovelDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
