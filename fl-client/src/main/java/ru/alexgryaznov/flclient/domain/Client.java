package ru.alexgryaznov.flclient.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Client {

    @Id
    @GeneratedValue
    private int id;

    private String url;

    private boolean online;
}
