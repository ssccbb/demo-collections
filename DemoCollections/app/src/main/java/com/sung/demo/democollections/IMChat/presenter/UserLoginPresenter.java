package com.sung.demo.democollections.IMChat.presenter;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.sung.demo.democollections.IMChat.model.IUserLogin;
import com.sung.demo.democollections.IMChat.model.OnLogoutListener;
import com.sung.demo.democollections.IMChat.model.OnRegistListener;
import com.sung.demo.democollections.IMChat.model.OnUserLoginListener;
import com.sung.demo.democollections.IMChat.model.UserLogin;
import com.sung.demo.democollections.IMChat.view.IUserLoginView;

/**
 * Created by sung on 2016/12/2.
 */

public class UserLoginPresenter {
    private IUserLoginView mIUserLoginView;
    private IUserLogin mIUserLogin;
    private Handler mHandler=new Handler();

    public UserLoginPresenter(IUserLoginView mIUserLoginView) {
        this.mIUserLoginView = mIUserLoginView;
        mIUserLogin=new UserLogin();
    }

    public void login(){
        if ( mIUserLoginView.getUserName()==null
                ||mIUserLoginView.getUserPassword()==null
                ||mIUserLoginView.getUserName().length()==0
                ||mIUserLoginView.getUserPassword().length()==0 ){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mIUserLoginView.afterLoginSuccess("请正确填入登陆信息！");
                }
            });
            Log.e("UserLoginPresenter", "login: "+"edit msg null");
            return;
        }

        Log.e("UserLoginPresenter", "login: "+mIUserLoginView.getUserName()+"/"+mIUserLoginView.getUserPassword());
        mIUserLogin.login(mIUserLoginView.getUserName().toString(),
                mIUserLoginView.getUserPassword().toString(),
                new OnUserLoginListener() {
            @Override
            public void loginSuccess(final String msg) {
                Log.e("UserLoginPresenter", "login: "+"msg");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIUserLoginView.afterLoginSuccess(msg);
                    }
                });
            }

            @Override
            public void loginFailed(final String msg) {
                Log.e("UserLoginPresenter", "login: "+"msg");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIUserLoginView.afterLogiFailed(msg);
                    }
                });
            }
        });
    }

    public void regist(){
        mIUserLogin.regist(mIUserLoginView.getUserName().toString(),
                mIUserLoginView.getUserPassword().toString(), new OnRegistListener() {
                    @Override
                    public void registSuccess(final String msg) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mIUserLoginView.registSuccess(msg);
                            }
                        });
                    }

                    @Override
                    public void registFailed(final String msg) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mIUserLoginView.registFailed(msg);
                            }
                        });
                    }
                });
    }

    public void logout(){
        mIUserLogin.logout(new OnLogoutListener() {
            @Override
            public void logoutSuccess() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIUserLoginView.logoutSuccess();
                    }
                });
            }

            @Override
            public void logoutFailed(final int code, final String message) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIUserLoginView.logoutFailed(code,message);
                    }
                });
            }
        });
    }

    public void checkMsgList(Activity context){
        mIUserLogin.checkMsgList(context);
    }

    public void toChatFriend(Activity context,String friName){
        mIUserLogin.toChatFriend(context, friName);
    }

    public boolean isLogin(){
        return EMClient.getInstance().isLoggedInBefore();
    }
}
