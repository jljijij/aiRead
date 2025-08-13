export interface INovelTag {
  id?: number;
  tagId?: string;
  tagName?: string;
  category?: string;
  isHot?: boolean | null;
  createTime?: Date;
  updateTime?: Date;
}

export class NovelTag implements INovelTag {
  constructor(
    public id?: number,
    public tagId?: string,
    public tagName?: string,
    public category?: string,
    public isHot?: boolean | null,
    public createTime?: Date,
    public updateTime?: Date,
  ) {
    this.isHot = this.isHot ?? false;
  }
}
