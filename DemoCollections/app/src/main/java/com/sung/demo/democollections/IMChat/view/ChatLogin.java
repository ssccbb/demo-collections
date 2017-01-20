package com.sung.demo.democollections.IMChat.view;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sung.demo.democollections.App;
import com.sung.demo.democollections.IMChat.presenter.UserLoginPresenter;
import com.sung.demo.democollections.R;

public class ChatLogin extends AppCompatActivity implements View.OnClickListener,IUserLoginView{
    private TextView msgList,chatWho,chatTo,loginResult,logout;
    private EditText loginUser,loginPassword;
    private Button login,regist;
    private UserLoginPresenter presenter=new UserLoginPresenter(this);
    private String TAG="View-ChatLogin";

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
        chatTo= (TextView) findViewById(R.id.chat_to);
        msgList.setOnClickListener(this);
        chatWho.setOnClickListener(this);
        chatTo.setOnClickListener(this);
        logout.setOnClickListener(this);

        loginResult= (TextView) findViewById(R.id.result);

        LoginStatus(presenter.isLogin());
        Log.e(TAG, "initUI: "+presenter.isLogin());
    }

    private void LoginStatus(boolean isLogin){
        if (!isLogin)
            return;

        loginResult.append("当前状态已登陆！");
        loginUser.setVisibility(View.INVISIBLE);
        loginPassword.setVisibility(View.INVISIBLE);
        login.setVisibility(View.INVISIBLE);
        regist.setVisibility(View.INVISIBLE);
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
                if (!presenter.isLogin()) {
                    noneLogin();
                    return;
                }
                presenter.logout();

                loginUser.setVisibility(View.VISIBLE);
                loginPassword.setVisibility(View.VISIBLE);
                login.setVisibility(View.VISIBLE);
                regist.setVisibility(View.VISIBLE);
                break;
            case R.id.chat:
                if (!presenter.isLogin()) {
                    noneLogin();
                    return;
                }

                presenter.toChatFriend(this,"sung1");
                break;
            case R.id.chat_to:
                if (!presenter.isLogin()) {
                    noneLogin();
                    return;
                }

                Log.e(TAG, "initUI: "+presenter.isLogin());
                final EditText editText = new EditText(ChatLogin.this);
                editText.setPadding(20,20,20,20);
                editText.setHint("输入想要聊天的对象名字");
                new AlertDialog.Builder(this)
                        .setMessage("指定聊天")
                        .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (editText.getText()==null
                                ||editText.getText().toString().length()==0)
                            return;

                        presenter.toChatFriend(ChatLogin.this,editText.getText().toString());
                    }
                }).show();
                break;
            case R.id.msglist:
                if (!presenter.isLogin()) {
                    noneLogin();
                    return;
                }

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

    private void noneLogin(){
        Toast.makeText(this, "user not login", Toast.LENGTH_SHORT).show();
    }
}
