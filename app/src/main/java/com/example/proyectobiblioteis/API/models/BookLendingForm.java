package com.example.proyectobiblioteis.API.models;

import com.google.gson.annotations.SerializedName;

public class BookLendingForm {

    @SerializedName("userId")
    private int userId;


    @SerializedName("bookId")
    private int bookId;

    public BookLendingForm(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
