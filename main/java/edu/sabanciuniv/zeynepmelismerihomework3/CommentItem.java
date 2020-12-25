package edu.sabanciuniv.zeynepmelismerihomework3;

/**
 * Created by atanaltay on 28/03/2017.
 */

public class CommentItem {

    private int id;
    private String name;
    private String message;


    public CommentItem() {
    }

    public CommentItem(int id,String name, String message) {
        this.name = name;
        this.message = message;
        this.id = id;
    }

    public CommentItem(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
