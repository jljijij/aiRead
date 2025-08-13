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
}
