package com.shanzha.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Novel.
 */
@Entity
@Table(name = "novel")
public class Novel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
