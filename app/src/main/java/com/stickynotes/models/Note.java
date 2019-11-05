package com.stickynotes.models;


import com.stickynotes.Constants;

import java.util.HashMap;
import java.util.Map;

/*
*  This class basically is a representation of a document in firebase firestore
* */
public class Note {
    private String title;
    private String note;
    private String datetime;
    private String id;

    public Note() {}

    public Note(String title, String note, String datetime) {
        this.title = title;
        this.note = note;
        this.datetime = datetime;
    }

    public Note(String id, String title, String note, String datetime) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", datetime='" + datetime + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public Map<String, String> toMap() {
        java.util.Map<java.lang.String, java.lang.String> data = new HashMap<>();
        data.put(Constants.FIRESTORE_NOTE_TITLE_KEY, this.getTitle());
        data.put(Constants.FIRESTORE_NOTE_KEY, this.getNote());
        data.put(
                Constants.FIRESTORE_NOTE_DATETIME_KEY,
                this.getDatetime() != null ? this.getDatetime() : ""
        );

        return data;
    }
}
