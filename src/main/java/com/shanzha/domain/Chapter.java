package com.shanzha.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * A Chapter.
 */
@Entity
@Table(name = "chapter")
public class Chapter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
