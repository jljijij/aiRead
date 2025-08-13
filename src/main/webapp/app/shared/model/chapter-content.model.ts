import { type IChapter } from '@/shared/model/chapter.model';

export interface IChapterContent {
  id?: number;
  novelId?: number;
  chapterId?: number;
  pageId?: number;
  compressedContentType?: string;
  compressed?: string;
  compressionType?: number | null;
  hash?: string | null;
  createTime?: Date;
  chapter?: IChapter | null;
}

export class ChapterContent implements IChapterContent {
  constructor(
    public id?: number,
    public novelId?: number,
    public chapterId?: number,
    public pageId?: number,
    public compressedContentType?: string,
    public compressed?: string,
    public compressionType?: number | null,
    public hash?: string | null,
    public createTime?: Date,
    public chapter?: IChapter | null,
  ) {}
}
