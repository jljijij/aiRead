export interface INovel {
  id?: number;
  title?: string;
  authorId?: number;
  coverUrl?: string | null;
  description?: string | null;
  categoryId?: number;
  tags?: string | null;
  wordCount?: number | null;
  chapterCount?: number | null;
  status?: number;
  isVip?: boolean | null;
  createTime?: Date;
  updateTime?: Date;
}

export class Novel implements INovel {
  constructor(
    public id?: number,
    public title?: string,
    public authorId?: number,
    public coverUrl?: string | null,
    public description?: string | null,
    public categoryId?: number,
    public tags?: string | null,
    public wordCount?: number | null,
    public chapterCount?: number | null,
    public status?: number,
    public isVip?: boolean | null,
    public createTime?: Date,
    public updateTime?: Date,
  ) {
    this.isVip = this.isVip ?? false;
  }
}
