package com.sung.demo.democollections.IMChat.model;

/**
 * Created by sung on 2016/12/2.
 */

public interface OnLogoutListener {
    void logoutSuccess();
    void logoutFailed(int code,String message);
}
