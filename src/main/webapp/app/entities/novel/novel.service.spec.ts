import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import NovelService from './novel.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Novel } from '@/shared/model/novel.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Novel Service', () => {
    let service: NovelService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new NovelService();
      currentDate = new Date();
      elemDefault = new Novel(123, 'AAAAAAA', 0, 'AAAAAAA', 'AAAAAAA', 0, 'AAAAAAA', 0, 0, 0, false, currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = {
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Novel', async () => {
        const returnedFromService = {
          id: 123,
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Novel', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Novel', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          authorId: 1,
          coverUrl: 'BBBBBB',
          description: 'BBBBBB',
          categoryId: 1,
          tags: 'BBBBBB',
          wordCount: 1,
          chapterCount: 1,
          status: 1,
          isVip: true,
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Novel', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Novel', async () => {
        const patchObject = {
          title: 'BBBBBB',
          description: 'BBBBBB',
          tags: 'BBBBBB',
          wordCount: 1,
          status: 1,
          isVip: true,
          ...new Novel(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Novel', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Novel', async () => {
        const returnedFromService = {
          title: 'BBBBBB',
          authorId: 1,
          coverUrl: 'BBBBBB',
          description: 'BBBBBB',
          categoryId: 1,
          tags: 'BBBBBB',
          wordCount: 1,
          chapterCount: 1,
          status: 1,
          isVip: true,
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Novel', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Novel', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Novel', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
