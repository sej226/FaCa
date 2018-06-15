package com.faca.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private String name;

    public boolean equals(String categoryName) {
        return name.equals(categoryName);
    }
}
