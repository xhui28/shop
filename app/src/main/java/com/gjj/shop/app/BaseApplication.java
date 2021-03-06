package com.gjj.shop.app;

import android.app.Application;

import com.gjj.applibrary.app.AppLib;
import com.gjj.applibrary.http.model.BundleKey;
import com.gjj.applibrary.task.ForegroundTaskExecutor;
import com.gjj.applibrary.util.AndroidUtil;
import com.gjj.applibrary.util.PreferencesManager;
import com.gjj.applibrary.util.Util;
import com.gjj.shop.user.UserMgr;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cookie.store.MemoryCookieStore;
import com.lzy.okhttputils.model.HttpHeaders;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2016/7/10.
 */
public class BaseApplication extends Application {
    private static BaseApplication mApp;
    private AppLib mAppLib;
    private UserMgr mUserMgr;
    public static BaseApplication getInstance() {
        return mApp;
    }
    public static final int DEFAULT_MILLISECONDS = 60000; //默认的超时时间

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mAppLib = AppLib.onCreate(mApp);
//        PreferencesManager.getInstance().put(BundleKey.TOKEN, "834320403214");
        OkHttpUtils.init(this);
        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.HEAD_KEY_CONNECTION, HttpHeaders.HEAD_VALUE_CONNECTION_KEEP_ALIVE);
        OkHttpUtils.getInstance()
                .addCommonHeaders(headers)
                .setConnectTimeout(DEFAULT_MILLISECONDS)  //全局的连接超时时间
                .setReadTimeOut(DEFAULT_MILLISECONDS)     //全局的读取超时时间
                .setWriteTimeOut(DEFAULT_MILLISECONDS)    //全局的写入超时时间
        .setCookieStore(new MemoryCookieStore());
        CrashReport.initCrashReport(this, "9cce669180", true);
        StringBuilder sb = Util.getThreadSafeStringBuilder();
        sb.append(AndroidUtil.getVersionName(this)).append('_').append(AndroidUtil.getVersionCode(this));
        CrashReport.setAppVersion(this, sb.toString());
        ForegroundTaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                mUserMgr = new UserMgr();
                AppLib.setInitialized(true);
            }
        });
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
    }
    public static UserMgr getUserMgr() {
        return mApp.mUserMgr;
    }
}

