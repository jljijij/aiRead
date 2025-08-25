package com.shanzha.service.dto;

public class ArticleNotifyMessage {

    private Long articleId;
    private String type;

    public ArticleNotifyMessage() {}

    public ArticleNotifyMessage(Long articleId, String type) {
        this.articleId = articleId;
        this.type = type;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
