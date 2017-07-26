package com.example.pc.resttest1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;

public class MemUpdateActivity extends AppCompatActivity {

    private EditText mEdtUpName, mEdtUpId, mEdtUpPw, mEdtUpHp;
    ImageView mimgProflie;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem_update);

        MemberBean.MemberBeanSub bean = (MemberBean.MemberBeanSub) getIntent().getSerializableExtra("memberBean");

        mimgProflie = (ImageView) findViewById(R.id.imgProfile);
        mEdtUpName = (EditText) findViewById(R.id.edtUpName);
        mEdtUpId = (EditText) findViewById(R.id.edtUpId);
        mEdtUpPw = (EditText) findViewById(R.id.edtUpPw);
        mEdtUpHp = (EditText) findViewById(R.id.edtUpHp);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar2);

        mEdtUpName.setText(bean.getName());
        mEdtUpId.setText(bean.getUserId());
        mEdtUpPw.setText(bean.getUserPw());
        mEdtUpHp.setText(bean.getHp());
        new ImageLoaderTask(mimgProflie).execute(Constants.BASE_URL + bean.getProfileImg());

        findViewById(R.id.btnUpOk).setOnClickListener(btnUpOkClick);
    }



    private View.OnClickListener btnUpOkClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new UpTask().execute();
        }
    };  //end btnUpOkClick


    private class UpTask extends AsyncTask<String, Void, String> {
        public static final String URL_UP_PROC=Constants.BASE_URL+"rest/updateMember.do";
        private String userName, userId, userPw, userHp;

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);

            userName = mEdtUpName.getText().toString();
            userId = mEdtUpId.getText().toString();
            userPw = mEdtUpPw.getText().toString();
            userHp = mEdtUpHp.getText().toString();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
                //map.add(" " <- 이부분은 memberBean의 이름과 같게 해주어야함!!!!! 꼭!!!!!!!
                map.add("userId", userId);
                map.add("userPw", userPw);
                map.add("name", userName);
                map.add("hp", userHp);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.ALL.APPLICATION_FORM_URLENCODED);
                HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

                return restTemplate.postForObject(URL_UP_PROC, request, String.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }   //end doInBackground

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.INVISIBLE);
            Gson gson = new Gson();
            try{
                MemberBean bean = gson.fromJson(s, MemberBean.class);
                if(bean!=null){
                    if(bean.getResult().equals("ok")){
                         finish();
                    }else {
                        Toast.makeText(MemUpdateActivity.this, bean.getResultMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                Toast.makeText(MemUpdateActivity.this, "파싱실패", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }//end onPostExecute

    }//end Task


    class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView dispImgView;

        public ImageLoaderTask(ImageView dispImgView) {
            this.dispImgView = dispImgView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgUrl = params[0];

            Bitmap bmp = null;

            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(imgUrl).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }//end doInBackground()

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                //표시
                dispImgView.setImageBitmap(bitmap);
            }
        }//end onPostExecute
    }//end class ImageLoaderTask

}
