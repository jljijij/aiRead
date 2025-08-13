import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ChapterDetails from './chapter-details.vue';
import ChapterService from './chapter.service';
import AlertService from '@/shared/alert/alert.service';

type ChapterDetailsComponentType = InstanceType<typeof ChapterDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const chapterSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Chapter Management Detail Component', () => {
    let chapterServiceStub: SinonStubbedInstance<ChapterService>;
    let mountOptions: MountingOptions<ChapterDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      chapterServiceStub = sinon.createStubInstance<ChapterService>(ChapterService);

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
          chapterService: () => chapterServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        chapterServiceStub.find.resolves(chapterSample);
        route = {
          params: {
            chapterId: `${123}`,
          },
        };
        const wrapper = shallowMount(ChapterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.chapter).toMatchObject(chapterSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        chapterServiceStub.find.resolves(chapterSample);
        const wrapper = shallowMount(ChapterDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
