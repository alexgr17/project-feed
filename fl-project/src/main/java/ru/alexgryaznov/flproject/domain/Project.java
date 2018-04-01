package ru.alexgryaznov.flproject.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Project {

    @Id
    private String guid;

    private String title;

    private String link;

    @Column(length = 10_000)
    private String description;

    @Column(length = 10_000)
    private String content;

    @OneToMany(mappedBy = "project")
    private List<Category> categories;

    @ManyToOne
    private RssFeed rssFeed;

    private Date pubDate;

    private boolean wasRead;

    @Transient
    private boolean hasStopWordInTitle;

    @Transient
    private List<String> stopWordMatchesInContent;
}
