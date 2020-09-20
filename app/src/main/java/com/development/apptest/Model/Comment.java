package com.development.apptest.Model;

public class Comment {
    private String comment;
    private String publisher;
    private String commentid;
    private String sentiment;



    public Comment(String comment, String publisher, String commentid, String sentiment) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.sentiment = sentiment;
    }

    public Comment() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getSentiment() { return sentiment; }

    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
}
