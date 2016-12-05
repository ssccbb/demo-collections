package com.sung.demo.democollections.IMChat.model;

/**
 * Created by sung on 2016/12/2.
 */

public interface OnUserLoginListener {
    void loginSuccess(String msg);
    void loginFailed(String msg);
}
