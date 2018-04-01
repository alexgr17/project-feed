package ru.alexgryaznov.flproject.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class RssFeed {

    @Id
    @GeneratedValue
    private int id;

    private String title;

    private String url;

    private String type;
}
