package com.shanzha.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.shanzha.domain.UserAiHistory} entity.
 */
@Schema(description = "AI 历史消息表")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserAiHistoryDTO implements Serializable {

    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String question;

    @NotNull
    private String answer;

    @NotNull
    private String chatId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAiHistoryDTO)) {
            return false;
        }

        UserAiHistoryDTO userAiHistoryDTO = (UserAiHistoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userAiHistoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAiHistoryDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", question='" + getQuestion() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", chatId='" + getChatId() + "'" +
            "}";
    }
}
