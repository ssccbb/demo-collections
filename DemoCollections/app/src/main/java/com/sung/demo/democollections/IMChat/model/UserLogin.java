package com.sung.demo.democollections.IMChat.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.sung.demo.democollections.IMChat.view.ChatMsgList;
import com.sung.demo.democollections.IMChat.view.SingleChatAct;

/**
 * Created by sung on 2016/12/2.
 */

public class UserLogin implements IUserLogin{
    @Override
    public void login(final String name, String password, final OnUserLoginListener onUserLoginListener) {
        Log.e("UserLogin", "login: "+name+"/"+password);
        EMClient.getInstance().login(name, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                onUserLoginListener.loginSuccess(name+"登录聊天服务器成功！");
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, final String message) {
                onUserLoginListener.loginFailed(
                        name+"登录聊天服务器失败！\n"+message+"\nLogin Failed code - "+code);
            }
        });
    }

    @Override
    public void regist(final String name, final String password, final OnRegistListener onRegistListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(name,password);
                    onRegistListener.registSuccess(name+"\t注册成功！");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    onRegistListener.registFailed("注册失败！"+e.toString());
                }
            }
        }).start();
    }

    @Override
    public void logout(final OnLogoutListener onLogoutListener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        onLogoutListener.logoutSuccess();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        onLogoutListener.logoutFailed(code,message);
                    }
                });
            }
        });
    }

    @Override
    public void checkMsgList(Activity context) {
        context.startActivity(new Intent(context,ChatMsgList.class));
    }

    @Override
    public void toChatFriend( Activity context,String friName) {
        context.startActivity(new Intent().
                setClass(context,SingleChatAct.class)
                .putExtra(EaseConstant.EXTRA_USER_ID,friName)
                .putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_SINGLE));
    }
}
