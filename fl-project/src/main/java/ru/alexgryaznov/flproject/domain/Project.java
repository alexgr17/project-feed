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

    //TODO this field is unused - remove
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    private List<Category> categories;

    @ManyToOne
    private RssFeed rssFeed;

    private Date pubDate;

    //TODO remove read projects >1 week
    private boolean wasRead;

    @Transient
    private boolean hasKeyWordInTitle;

    @Transient
    private boolean hasStopWordInTitle;

    @Transient
    private List<String> keyWordMatchesInContent;

    @Transient
    private List<String> stopWordMatchesInContent;

    public boolean isHasKeyWord() {
        return hasKeyWordInTitle || (keyWordMatchesInContent != null && !keyWordMatchesInContent.isEmpty());
    }

    public boolean isFiltered() {
        return hasStopWordInTitle && !isHasKeyWord();
    }
}
