import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ChapterContentDetails from './chapter-content-details.vue';
import ChapterContentService from './chapter-content.service';
import AlertService from '@/shared/alert/alert.service';

type ChapterContentDetailsComponentType = InstanceType<typeof ChapterContentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const chapterContentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ChapterContent Management Detail Component', () => {
    let chapterContentServiceStub: SinonStubbedInstance<ChapterContentService>;
    let mountOptions: MountingOptions<ChapterContentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      chapterContentServiceStub = sinon.createStubInstance<ChapterContentService>(ChapterContentService);

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
          chapterContentService: () => chapterContentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        chapterContentServiceStub.find.resolves(chapterContentSample);
        route = {
          params: {
            chapterContentId: `${123}`,
          },
        };
        const wrapper = shallowMount(ChapterContentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.chapterContent).toMatchObject(chapterContentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        chapterContentServiceStub.find.resolves(chapterContentSample);
        const wrapper = shallowMount(ChapterContentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
