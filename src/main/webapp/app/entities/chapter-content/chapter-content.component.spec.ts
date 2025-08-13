import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ChapterContent from './chapter-content.vue';
import ChapterContentService from './chapter-content.service';
import AlertService from '@/shared/alert/alert.service';

type ChapterContentComponentType = InstanceType<typeof ChapterContent>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ChapterContent Management Component', () => {
    let chapterContentServiceStub: SinonStubbedInstance<ChapterContentService>;
    let mountOptions: MountingOptions<ChapterContentComponentType>['global'];

    beforeEach(() => {
      chapterContentServiceStub = sinon.createStubInstance<ChapterContentService>(ChapterContentService);
      chapterContentServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          chapterContentService: () => chapterContentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        chapterContentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(ChapterContent, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(chapterContentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.chapterContents[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ChapterContentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ChapterContent, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        chapterContentServiceStub.retrieve.reset();
        chapterContentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        chapterContentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeChapterContent();
        await comp.$nextTick(); // clear components

        // THEN
        expect(chapterContentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(chapterContentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
