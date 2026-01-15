package com.example.dailyquotes_1.model;

import com.google.gson.annotations.SerializedName;

/**
 * POJO class representing a Quote from the DummyJSON API.
 * Fields map the API payload: { id, quote, author }.
 */
public class Quote {

    // DummyJSON uses key "quote" for the content text
    @SerializedName("quote")
    private String content;

    @SerializedName("author")
    private String author;

    // id is numeric in DummyJSON; keep as String for simplicity
    @SerializedName("id")
    private String id;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Quote() {
    }

    /**
     * Constructor with content and author.
     *
     * @param content The quote text
     * @param author The author of the quote
     */
    public Quote(String content, String author) {
        this.content = content;
        this.author = author;
    }

    // Getters and Setters

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
