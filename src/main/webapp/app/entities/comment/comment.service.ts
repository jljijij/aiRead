import axios from 'axios';

export default class CommentService {
  public retrieve(): Promise<any> {
    return axios.get('api/comments');
  }
}
