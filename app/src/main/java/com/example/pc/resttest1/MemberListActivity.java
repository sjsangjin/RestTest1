package com.example.pc.resttest1;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;

public class MemberListActivity extends AppCompatActivity {

    private ListView mListView;
    private MemberListAdapter memberListAdapter;
//    public int mCurrentX, mCurrentY;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

//        List<MemberBean.MemberBeanSub> list = (List<MemberBean.MemberBeanSub>)
//        getIntent().getSerializableExtra(Constants.INTENT_KEY_MEMBER_LIST);

        mListView = (ListView)findViewById(R.id.listView);
        memberListAdapter = new MemberListAdapter(this);
        mListView.setAdapter(memberListAdapter);

    }

    @Override
    protected void onResume(){
        super.onResume();
        memberListAdapter.updateMemberListTask(); //데이터 갱신
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(pos);
            }
        },1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        pos = mListView.getFirstVisiblePosition();

    }
}
