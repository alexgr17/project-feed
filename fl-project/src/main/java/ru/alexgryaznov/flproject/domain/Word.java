package ru.alexgryaznov.flproject.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode(exclude = "pattern")
@MappedSuperclass
public abstract class Word {

    private static final String CASE_INSENSITIVE_PREFIX = "(?i)";

    @Id
    @GeneratedValue
    protected int id;

    protected String title;

    protected Integer sort;

    @Transient
    protected Pattern pattern;

    public Word() {
        // empty
    }

    public Word(String title) {
        this.title = title;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(CASE_INSENSITIVE_PREFIX + Pattern.quote(title), Pattern.UNICODE_CASE);
        }
        return pattern;
    }
}
