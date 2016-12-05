package com.sung.demo.democollections.IMChat.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.sung.demo.democollections.R;

/*
* 消息列表界面
* */
public class ChatMsgList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imchat_chatmsg_list);
        getSupportActionBar().hide();
        addMsgListView();
    }

    private void addMsgListView(){
        EaseConversationListFragment msgList = new EaseConversationListFragment();
        msgList.setConversationListItemClickListener(new EaseConversationListFragment.EaseConversationListItemClickListener() {

            @Override
            public void onListItemClicked(EMConversation conversation) {
                startActivity(new Intent(ChatMsgList.this, SingleChatAct.class)
                        .putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, msgList).commit();
    }
}
