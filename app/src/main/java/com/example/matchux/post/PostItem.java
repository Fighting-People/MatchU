package com.example.matchux.post;

public class PostItem {

    String title;
    String content;

    public PostItem() {}

    public PostItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}