import axios from 'axios';

const baseApiUrl = 'api/novels';

export default class NovelService {
  parse(file: File): Promise<string> {
    const formData = new FormData();
    formData.append('file', file);
    return new Promise((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/parse`, formData, { headers: { 'Content-Type': 'multipart/form-data' } })
        .then(res => resolve(res.data))
        .catch(err => reject(err));
    });
  }

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
