import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import NovelTagService from './novel-tag.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { NovelTag } from '@/shared/model/novel-tag.model';

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
  describe('NovelTag Service', () => {
    let service: NovelTagService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new NovelTagService();
      currentDate = new Date();
      elemDefault = new NovelTag(123, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', false, currentDate, currentDate);
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

      it('should create a NovelTag', async () => {
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

      it('should not create a NovelTag', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a NovelTag', async () => {
        const returnedFromService = {
          tagId: 'BBBBBB',
          tagName: 'BBBBBB',
          category: 'BBBBBB',
          isHot: true,
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

      it('should not update a NovelTag', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a NovelTag', async () => {
        const patchObject = { tagId: 'BBBBBB', updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT), ...new NovelTag() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a NovelTag', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of NovelTag', async () => {
        const returnedFromService = {
          tagId: 'BBBBBB',
          tagName: 'BBBBBB',
          category: 'BBBBBB',
          isHot: true,
          createTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          updateTime: dayjs(currentDate).format(DATE_TIME_FORMAT),
          ...elemDefault,
        };
        const expected = { createTime: currentDate, updateTime: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of NovelTag', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a NovelTag', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a NovelTag', async () => {
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
