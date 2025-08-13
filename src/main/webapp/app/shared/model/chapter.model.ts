import { type INovel } from '@/shared/model/novel.model';

export interface IChapter {
  id?: number;
  novelId?: number;
  chapterNo?: number;
  title?: string;
  contentId?: number;
  wordCount?: number | null;
  isVip?: boolean | null;
  price?: number | null;
  createTime?: Date;
  updateTime?: Date;
  novel?: INovel | null;
}

export class Chapter implements IChapter {
  constructor(
    public id?: number,
    public novelId?: number,
    public chapterNo?: number,
    public title?: string,
    public contentId?: number,
    public wordCount?: number | null,
    public isVip?: boolean | null,
    public price?: number | null,
    public createTime?: Date,
    public updateTime?: Date,
    public novel?: INovel | null,
  ) {
    this.isVip = this.isVip ?? false;
  }
}
