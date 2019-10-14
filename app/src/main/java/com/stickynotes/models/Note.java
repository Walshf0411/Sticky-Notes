package com.stickynotes.models;

public class Note {
    private String title;
    private String note;
    private String datetime;

    public Note() {}

    public Note(String title, String note, String datetime) {
        this.title = title;
        this.note = note;
        this.datetime = datetime;
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
                '}';
    }
}
