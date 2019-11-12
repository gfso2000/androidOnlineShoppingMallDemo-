package com.gfso.client.oauthclientapplication.fragment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.User;
import com.gfso.client.oauthclientapplication.fragment.widget.MyEditText;
import com.gfso.client.oauthclientapplication.msg.ResponseMsg;
import com.gfso.client.oauthclientapplication.okhttp.HttpLoadingDialog;
import com.gfso.client.oauthclientapplication.okhttp.OkhttpHelper;
import com.gfso.client.oauthclientapplication.util.Contents;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.email_login)
    TextView email_login ;
    @BindView(R.id.sms_login)
    TextView sms_login ;

    @BindView(R.id.email_login_layout)
    LinearLayout email_login_layout;
    @BindView(R.id.userId)
    MyEditText userId ;
    @BindView(R.id.password)
    MyEditText password ;
    @BindView(R.id.email_login_btn)
    Button email_login_btn;

    @BindView(R.id.sms_login_layout)
    LinearLayout sms_login_layout;
    @BindView(R.id.phone)
    MyEditText phone ;
    @BindView(R.id.sms_code)
    MyEditText sms_code ;
    @BindView(R.id.sms_login_btn)
    Button sms_login_btn ;
    @BindView(R.id.get_sms_code)
    Button get_sms_code ;

    OkhttpHelper okhttpHelper;
    Toolbar toolbar = null;
    private int maxCountDowntime = 6;
    private int currentCountDownTime = 6 ;
    private Timer timer ;
    private Handler mHandler ;
    private static final int HANDLER_CODE = 0X123 ;
    private EventHandler eh=new SMSEvenHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyMode();
                finish();//返回
            }
        });

        addListener();
        initView();

        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar != null) {
            toolbar.setTitle("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SMSSDK.unregisterEventHandler(eh);
    }

    protected void initView() {
        userId.setInputType(InputType.TYPE_CLASS_TEXT); //输入类型
        userId.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)}); //最大输入长度
        password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框
        okhttpHelper = OkhttpHelper.getOkhttpHelper() ;
        Text() ;
    }

    protected void addListener() {
        email_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin(v) ;
//                User user = new User();
//                user.setUsername("jack");
//                MyApplication.getInstance().putUser(user,"token");
//                Intent intent = new Intent();
//                Bundle conData = new Bundle();
//                conData.putString(Contents.LOGIN_USERID, "jack");
//                intent.putExtras(conData);
//                setResult(RESULT_OK, intent);
//                finish();
            }
        });

        email_login.setOnClickListener((v)->{
            email_login_layout.setVisibility(View.VISIBLE);
            email_login_btn.setText("邮箱登录");
            sms_login_layout.setVisibility(View.GONE);
        });

        sms_login.setOnClickListener((v)->{
            email_login_layout.setVisibility(View.GONE);
            sms_login_layout.setVisibility(View.VISIBLE);
            sms_login_btn.setText("手机短信登录");
        });

        sms_login_btn.setOnClickListener((v)->{
            doSMSLogin(v);
        });

        get_sms_code.setOnClickListener((v)->{
            getSMSCode();
            beginCountDown();
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if( HANDLER_CODE == msg.what ){
                    if(currentCountDownTime != 0) {
                        currentCountDownTime-- ;
                        get_sms_code.setText(currentCountDownTime + "秒后可重新发送");
                    }else{
                        get_sms_code.setText("点击重新发送");
                        stopCountDown() ;
                    }
                }
            }
        };
    }

    private void beginCountDown(){
        currentCountDownTime = maxCountDowntime;
        get_sms_code.setClickable(false);
        get_sms_code.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_btn_color_disable));
        timer = new Timer() ;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(HANDLER_CODE) ;
            }
        },0 ,1000);
    }

    private void stopCountDown(){
        timer.cancel();
        get_sms_code.setClickable(true);
        get_sms_code.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
    }

    private void getSMSCode() {
        SMSSDK.getVerificationCode("86", phone.getText().toString());
    }

    private void closeKeyMode(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(),0);
    }

    private void doLogin( final View v ){
        final String userName = userId.getText().toString() ;

        if ( userName == null ){
            Toast.makeText( v.getContext() , "请输入邮箱" , Toast.LENGTH_SHORT).show();
            return;
        }

        String pwd = password.getText().toString() ;
        if (pwd == null){
            Toast.makeText( v.getContext() , "请输入密码" , Toast.LENGTH_SHORT).show() ;
            return;
        }

        String uri = Contents.LOGIN_URL ;
        Map< String , String > params = new HashMap<String, String>() ;
        params.put("userId" , userName ) ;
        params.put("password" , pwd) ;

        okhttpHelper.doJSONPost(uri, new HttpLoadingDialog<ResponseMsg>(LoginActivity.this ) {
            @Override
            public void onError(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
                Toast.makeText(v.getContext(), response.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Request request, IOException e) {
                super.onFailure(request, e);
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void callBackSucces(Response response, ResponseMsg msg) throws IOException {
                this.closeSpotsDialog();
                closeKeyMode();

                if(msg.isSuccess()){
                    //save user token
                    String token = msg.getData().get("token");
                    User user = new User();
                    user.setUsername(userName);
                    MyApplication.getInstance().putUser(user,token);
                    //finish activity
                    Intent intent = new Intent();
                    Bundle conData = new Bundle();
                    conData.putString(Contents.LOGIN_USERID, userName);
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showLoginErrorMsg(msg.getErrorMessage()) ;
                }

//                if(userLoginRespMsg.getStatus() == 1){

//                    MyApplication.getInstance().putUser(userLoginRespMsg.getData() , userLoginRespMsg.getTocken());
//                    closeKeyMode() ;
//
//                    if (null == MyApplication.getInstance().getIntent()){
//                        setResult(RESULT_OK);
//                        finish();
//                    }else {
//                        MyApplication.jumpToTargetoActivity(LoginActivity.this);
//                        finish();
//                    }
//                }else {
//                    showLoginErrorMsg() ;
//                    userId.setText("");
//                    password.setText("");
//                }
            }
        }, params);
    }

    private void doSMSLogin(final View v){
        final String phoneNum = phone.getText().toString() ;

        if (TextUtils.isEmpty(phoneNum)){
            Toast.makeText( v.getContext() , "请输入手机" , Toast.LENGTH_SHORT).show();
            return;
        }

        String smsCode = sms_code.getText().toString() ;
        if (TextUtils.isEmpty(smsCode)){
            Toast.makeText( v.getContext() , "请输入验证码" , Toast.LENGTH_SHORT).show() ;
            return;
        }

        String uri = Contents.SMS_LOGIN_URL ;
        Map< String , String > params = new HashMap<String, String>() ;
        params.put("phoneNum" , phoneNum ) ;
        params.put("smsCode" , smsCode) ;

        okhttpHelper.doJSONPost(uri, new HttpLoadingDialog<ResponseMsg>(LoginActivity.this ) {
            @Override
            public void onError(Response response, int responseCode, Exception e) throws IOException {
                this.closeSpotsDialog();
                Toast.makeText(v.getContext(), response.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Request request, IOException e) {
                super.onFailure(request, e);
                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void callBackSucces(Response response, ResponseMsg msg) throws IOException {
                this.closeSpotsDialog();
                closeKeyMode();

                if(msg.isSuccess()){
                    //save user token
                    String token = msg.getData().get("token");
                    User user = new User();
                    user.setUsername(phoneNum);
                    MyApplication.getInstance().putUser(user,token);
                    //finish activity
                    Intent intent = new Intent();
                    Bundle conData = new Bundle();
                    conData.putString(Contents.LOGIN_USERID, phoneNum);
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showLoginErrorMsg(msg.getErrorMessage()) ;
                }
            }
        }, params);
    }

    public void Text(){
        userId.setText("jack.yu@gmail.com");
        password.setText("123456");
        phone.setText("13411112222");
    }

    private void showLoginErrorMsg(String msg){
        closeKeyMode();
        Toast.makeText(this, msg , Toast.LENGTH_LONG).show();
    }

    public class SMSEvenHandler extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (data instanceof Throwable) {
                        Throwable throwable = (Throwable) data;
                        String msg = throwable.getMessage();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                Log.d("", "run:获取验证码成功----------------------------- ");
                                //请求验证码之后，进行操作
                                Toast.makeText(LoginActivity.this, data.toString(), Toast.LENGTH_LONG).show();
                            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            }
                        }
                    }
                }
            });
        }
    }
}
