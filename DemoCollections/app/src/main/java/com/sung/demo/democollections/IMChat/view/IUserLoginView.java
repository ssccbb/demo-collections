package com.sung.demo.democollections.IMChat.view;

/**
 * Created by sung on 2016/12/2.
 */

public interface IUserLoginView {
    String getUserName();

    String getUserPassword();

    void afterLoginSuccess(String msg);

    void afterLogiFailed(String msg);

    void registSuccess(String msg);

    void registFailed(String msg);

    void logoutSuccess();

    void logoutFailed(int code,String msg);
}
