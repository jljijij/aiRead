import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Novel from './novel.vue';
import NovelService from './novel.service';
import AlertService from '@/shared/alert/alert.service';

type NovelComponentType = InstanceType<typeof Novel>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Novel Management Component', () => {
    let novelServiceStub: SinonStubbedInstance<NovelService>;
    let mountOptions: MountingOptions<NovelComponentType>['global'];

    beforeEach(() => {
      novelServiceStub = sinon.createStubInstance<NovelService>(NovelService);
      novelServiceStub.retrieve.resolves({ headers: {} });

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
          novelService: () => novelServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        novelServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Novel, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(novelServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.novels[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: NovelComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Novel, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        novelServiceStub.retrieve.reset();
        novelServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        novelServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeNovel();
        await comp.$nextTick(); // clear components

        // THEN
        expect(novelServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(novelServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
