package ru.alexgryaznov.flproject.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.regex.Pattern;

@Entity
@Data
//TODO rename table and remove table annotation
@Table(name = "keyword")
public class StopWord {

    private static final String CASE_INSENSITIVE_PREFIX = "(?i)";

    @Id
    @GeneratedValue
    private int id;

    private String title;

    private Integer sort;

    @Transient
    private Pattern pattern;

    public StopWord() {
        // empty
    }

    public StopWord(String title) {
        this.title = title;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(CASE_INSENSITIVE_PREFIX + Pattern.quote(title), Pattern.UNICODE_CASE);
        }
        return pattern;
    }
}
