package ru.alexgryaznov.flproject.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue
    private int id;

    private String title;

    @ManyToOne
    private Project project;
}
