package com.sung.demo.democollections.IMChat.model;

import android.app.Activity;

/**
 * Created by sung on 2016/12/2.
 */

public interface IUserLogin {
    void login(String name,String password,OnUserLoginListener onUserLoginListener);

    void regist(String name,String password,OnRegistListener onRegistListener);

    void logout(OnLogoutListener onLogoutListener);

    void checkMsgList(Activity context);

    void toChatFriend(Activity context,String friName);
}
