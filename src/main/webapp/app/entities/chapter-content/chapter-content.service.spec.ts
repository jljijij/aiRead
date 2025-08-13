import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import ChapterContentService from './chapter-content.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { ChapterContent } from '@/shared/model/chapter-content.model';

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
  describe('ChapterContent Service', () => {
    let service: ChapterContentService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new ChapterContentService();
      currentDate = new Date();
      elemDefault = new ChapterContent(123, 0, 0, 0, 'image/png', 'AAAAAAA', 0, 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { createTime: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
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

      it('should create a ChapterContent', async () => {
        const returnedFromService = { id: 123, createTime: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { createTime: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a ChapterContent', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a ChapterContent', async () => {
        const returnedFromService = {
          novelId: 1,
          chapterId: 1,
          pageId: 1,
          compressed: 'BBBBBB',
          compressionType: 1,
          hash: 'BBBBBB',
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };

        const expected = { createTime: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a ChapterContent', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a ChapterContent', async () => {
        const patchObject = {
          novelId: 1,
          hash: 'BBBBBB',
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...new ChapterContent(),
        };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { createTime: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a ChapterContent', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of ChapterContent', async () => {
        const returnedFromService = {
          novelId: 1,
          chapterId: 1,
          pageId: 1,
          compressed: 'BBBBBB',
          compressionType: 1,
          hash: 'BBBBBB',
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createTime: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of ChapterContent', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a ChapterContent', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a ChapterContent', async () => {
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
