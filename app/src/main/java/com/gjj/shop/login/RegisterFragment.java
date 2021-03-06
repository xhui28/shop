package com.gjj.shop.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.gjj.applibrary.http.callback.JsonCallback;
import com.gjj.applibrary.http.model.BundleKey;
import com.gjj.applibrary.log.L;
import com.gjj.applibrary.util.MD5Util;
import com.gjj.applibrary.util.PreferencesManager;
import com.gjj.applibrary.util.ToastUtil;
import com.gjj.applibrary.util.Util;
import com.gjj.shop.R;
import com.gjj.shop.app.BaseApplication;
import com.gjj.shop.base.BaseFragment;
import com.gjj.shop.main.MainActivity;
import com.gjj.shop.model.UserInfo;
import com.gjj.shop.net.ApiConstants;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpHeaders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chuck on 16/7/17.
 */
public class RegisterFragment extends BaseFragment {

    private boolean mIsRegister;
    public static String FLAG = "from";

    /**
     * 输入手机号
     */
    @Bind(R.id.et_username)
    EditText mUsername;
    /**
     * 密码
     */
    @Bind(R.id.et_psw)
    EditText mPsw;
    /**
     * 确认密码
     */
    @Bind(R.id.et_psw_check)
    EditText mPswCheck;

    @Bind(R.id.send_again_btn)
    Button mGetSmsBtn;
    /**
     * 密码
     */
    @Bind(R.id.et_sms_code)
    EditText mSmsCode;

    @Bind(R.id.btn_register)
    Button mRegister;
    /**
     * 短信验证有效时间ms
     */
    private static final int SMS_VALIDITY = 60000;
    private InputMethodManager mInputMethodManager;
    private Counter mCounter;


    @OnClick(R.id.send_again_btn)
    void onGetSms() {
        String account = mUsername.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.shortToast(R.string.enter_mobile);
            return;
        }
        if (!Util.isMobileNO(account)) {
            ToastUtil.shortToast(R.string.enter_mobile_error);
            return;
        }
        EditText identifyCodeET = mSmsCode;
        identifyCodeET.setText("");
        identifyCodeET.setEnabled(true);
        identifyCodeET.requestFocus();
        hideKeyboardForCurrentFocus();
        sendGetSmsReq(account);
    }

    private void sendGetSmsReq(String account) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", account);
//        final JSONObject jsonObject = new JSONObject(params);
        OkHttpUtils.post(ApiConstants.GET_SMS_CODE)//
                .tag(this)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(params)
//                .postJson(jsonObject.toString())//
                .execute(new JsonCallback<String>(String.class) {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDownSms();
                            }
                        });
                    }

                    @Override
                    public void onError(Call call, final Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(response != null)
                                    L.d("@@@@@>>" + response.code() + "msg>>" + response.message());
                                ToastUtil.shortToast(R.string.fail);
                            }
                        });
                    }
                });
    }

    @OnClick(R.id.btn_register)
    void register() {
        String account = mUsername.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtil.shortToast(R.string.enter_mobile);
            return;
        }
        String code = mSmsCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.shortToast(R.string.enter_sms);
            return;
        }
        String psw = mPsw.getText().toString().trim();
        String pswCheck = mPswCheck.getText().toString().trim();
        if(!psw.equals(pswCheck)) {
            ToastUtil.shortToast(R.string.enter_psw_error);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("username", account);
        params.put("password", MD5Util.md5Hex(psw));
        params.put("phoneCode",code);
//        final JSONObject jsonObject = new JSONObject(params);
        if(mIsRegister) {
            register(params);
        } else {
            findPsw(params);
        }

    }

    private void findPsw(HashMap<String, String> params) {
        showLoadingDialog(R.string.committing, false);
        OkHttpUtils.post(ApiConstants.FIND_PSW)//
                .tag(this)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(params)
//                .postJson(jsonObject.toString())//
                .execute(new JsonCallback<UserInfo>(UserInfo.class) {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final UserInfo userInfo, Call call, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                if(userInfo != null) {
                                    Activity activity = getActivity();
                                    L.d("@@@@@>>" + userInfo.token);
                                    BaseApplication.getUserMgr().saveUserInfo(userInfo);
                                    Intent intent = new Intent();
                                    intent.setClass(activity, MainActivity.class);
                                    startActivity(intent);
                                    activity.finish();
                                }
                            }
                        });
                    }

                });
    }
    private void register(HashMap<String, String> params) {
        showLoadingDialog(R.string.committing, false);
        OkHttpUtils.post(ApiConstants.REGISTER)//
                .tag(this)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(params)
//                .postJson(jsonObject.toString())//
                .execute(new JsonCallback<UserInfo>(UserInfo.class) {
                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final UserInfo userInfo, Call call, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                if(userInfo != null) {
                                    Activity activity = getActivity();
                                    L.d("@@@@@>>" + userInfo.token);
                                    ToastUtil.shortToast(R.string.register_success);
                                    PreferencesManager.getInstance().put(BundleKey.TOKEN, userInfo.token);
                                    Intent intent = new Intent();
                                    intent.setClass(activity, MainActivity.class);
                                    startActivity(intent);
                                    activity.finish();
                                }
                            }
                        });
                    }
                });
    }
    /**
     * 短信按钮不可点击
     *
     * @param text
     */
    private void disableGetSmsBtn(String text) {
        Button getSmsBtn = mGetSmsBtn;
        getSmsBtn.setEnabled(false);
        if (null != text) {
            getSmsBtn.setText(text);
        }
        getSmsBtn.setTextColor(getResources().getColor(R.color.secondary_gray));
    }

    @Override
    public int getContentViewLayout() {
        return R.layout.fragment_register;
    }

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        mIsRegister = bundle.getBoolean(FLAG);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    /**
     * 短信重新发送倒计时
     */
    private void countDownSms() {
        disableGetSmsBtn(null);
        // mIdentifyCodeET.setEnabled(true);
        // mIdentifyCodeET.requestFocus();
        mInputMethodManager.showSoftInput(mSmsCode, InputMethodManager.SHOW_IMPLICIT);
        if (mCounter != null) {
            mCounter.cancel();
        }
        mCounter = new Counter(SMS_VALIDITY, 1000l);
        mCounter.start();
    }

    public void hideKeyboardForCurrentFocus() {
        if (getActivity().getCurrentFocus() != null) {
            mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    /* 定义一个倒计时的内部类 */
    class Counter extends CountDownTimer {
        public Counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (getActivity() == null) {
                return;
            }
            enableGetSmsBtn(getString(R.string.send_sms_again));
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (getActivity() == null) {
                return;
            }
            mGetSmsBtn.setText(sendSmsCountDown(millisUntilFinished / 1000l));
        }
    }

    /**
     * 短信按钮可点击
     *
     * @param text
     */
    private void enableGetSmsBtn(String text) {
        Button getSmsBtn = mGetSmsBtn;
        getSmsBtn.setEnabled(true);
        if (null != text) {
            getSmsBtn.setText(text);
        }
        getSmsBtn.setTextColor(getResources().getColor(R.color.secondary_gray));
    }


    /**
     * 设置按钮可用
     *
     * @param ms
     */


    private String sendSmsCountDown(long ms) {
        StringBuilder tip = Util.getThreadSafeStringBuilder();
        return tip.append("再次发送").append("(").append(ms).append(")").toString();
    }
}
