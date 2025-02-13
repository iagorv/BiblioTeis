package com.example.proyectobiblioteis.API.models;

public class BookLendingForm {

    private int UserId;
    private int BookId;

    public BookLendingForm(int userId, int bookId) {
        UserId = userId;
        BookId = bookId;
    }

    public int getBookId() {
        return BookId;
    }

    public void setBookId(int bookId) {
        BookId = bookId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }
}
