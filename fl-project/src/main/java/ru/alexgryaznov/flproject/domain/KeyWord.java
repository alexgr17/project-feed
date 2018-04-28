package ru.alexgryaznov.flproject.domain;

import javax.persistence.Entity;

@Entity
public class KeyWord extends Word {

    public KeyWord() {
        // empty
    }

    public KeyWord(String title) {
        super(title);
    }
}
