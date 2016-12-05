package com.sung.demo.democollections.IMChat.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sung.demo.democollections.App;
import com.sung.demo.democollections.IMChat.presenter.UserLoginPresenter;
import com.sung.demo.democollections.R;

public class ChatLogin extends AppCompatActivity implements View.OnClickListener,IUserLoginView{
    private TextView msgList,chatWho,loginResult,logout;
    private EditText loginUser,loginPassword;
    private Button login,regist;
    private UserLoginPresenter presenter=new UserLoginPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imchat_chat_login);
        initUI();
    }

    private void initUI(){
        getSupportActionBar().hide();

        loginUser= (EditText) findViewById(R.id.user);
        loginPassword= (EditText) findViewById(R.id.password);

        login= (Button) findViewById(R.id.login);
        regist= (Button) findViewById(R.id.regist);
        login.setOnClickListener(this);
        regist.setOnClickListener(this);

        logout= (TextView) findViewById(R.id.logout);
        msgList= (TextView) findViewById(R.id.msglist);
        chatWho= (TextView) findViewById(R.id.chat);
        msgList.setOnClickListener(this);
        chatWho.setOnClickListener(this);
        logout.setOnClickListener(this);

        loginResult= (TextView) findViewById(R.id.result);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                presenter.login();
                break;
            case R.id.regist:
                presenter.regist();
                break;
            case R.id.logout:
                presenter.logout();
                break;
            case R.id.chat:
                presenter.toChatFriend(this,"sung1");
                break;
            case R.id.msglist:
                presenter.checkMsgList(this);
                break;
        }
    }

    @Override
    public String getUserName() {
        return loginUser.getText().toString();
    }

    @Override
    public String getUserPassword() {
        return loginPassword.getText().toString();
    }

    @Override
    public void afterLoginSuccess(String msg) {
        loginResult.append("\n"+msg);
    }

    @Override
    public void afterLogiFailed(String msg) {
        loginResult.append("\n"+msg);
    }

    @Override
    public void registSuccess(String msg) {
        loginResult.append("\n"+msg);
    }

    @Override
    public void registFailed(String msg) {
        loginResult.append("\n"+msg);
    }

    @Override
    public void logoutSuccess() {
        loginResult.append("\n"+"当前账户退出成功！");
    }

    @Override
    public void logoutFailed(int code, String msg) {
        loginResult.append("\n"+"当前账户退出失败！"
                +"\nlogout failed code - "+code+"\nmsg - "+msg);
    }
}
