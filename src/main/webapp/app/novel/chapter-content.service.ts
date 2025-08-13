import axios from 'axios';

const baseApiUrl = 'api/chapter-contents';

export default class ChapterContentService {
  find(id: number): Promise<any> {
    return axios.get(`${baseApiUrl}/${id}`).then(res => res.data);
  }

  retrieve(): Promise<any[]> {
    return axios.get(baseApiUrl).then(res => res.data);
  }

  create(entity: any): Promise<any> {
    return axios.post(baseApiUrl, entity).then(res => res.data);
  }

  update(entity: any): Promise<any> {
    return axios.put(`${baseApiUrl}/${entity.id}`, entity).then(res => res.data);
  }

  delete(id: number): Promise<void> {
    return axios.delete(`${baseApiUrl}/${id}`);
  }
}
