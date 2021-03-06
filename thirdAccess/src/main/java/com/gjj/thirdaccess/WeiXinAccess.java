package com.gjj.thirdaccess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Title:过家家-项目经理
 * Description:
 * Copyright: Copyright (c) 2016
 * Company: 深圳市过家家
 * version: 1.0.0.0
 * author: jack
 * createDate 2016/2/17
 */
public class WeiXinAccess {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private   IWXAPI mIWXAPI;
    /**
    * 微信appid
    */
    public static  String APPID_WEIXIN = "";
    /**
     * 微信授权域
     */
    public static final String SCOPE_WEIXIN = "snsapi_userinfo";
    /**
     *
     */
    public static final String STATE_WEIXIN = "";

    private  Context mContext;
    public WeiXinAccess(Context context) {
        this.mContext = context;
        //initAPPID();
    }

    public WeiXinAccess(Context context, String appId) {
        this.mContext = context;
        APPID_WEIXIN = appId;
        getmIWXAPI();
        //initAPPID();
    }
    /**
     * 初始化APPID
     */
    public void initAPPID() {
        AppIdMgr appIdMgr = new AppIdMgr(mContext);
        APPID_WEIXIN = appIdMgr.getWEXINAPPID();
    }


    public  IWXAPI getmIWXAPI() {
        if (mIWXAPI == null) {
            mIWXAPI = WXAPIFactory.createWXAPI(mContext, APPID_WEIXIN, true);
            mIWXAPI.registerApp(APPID_WEIXIN);
        }
        return mIWXAPI;
    }

    /**
     *微信登录
     */
    public void loginWeiXin() {
        if (mIWXAPI == null) {
            getmIWXAPI();
        }
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(mContext, mContext.getString(R.string.weixin_not_install),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        /*req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";*/
        req.scope = SCOPE_WEIXIN;
        req.state = STATE_WEIXIN;
        mIWXAPI.sendReq(req);
    }

    public void share2weixin(boolean isTimeline, Bitmap bitmap, String url, String title, String desc) {
        if (mIWXAPI == null) {
            getmIWXAPI();
        }
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(mContext, mContext.getString(R.string.weixin_not_install),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webPage);

        msg.title = title;
        msg.description = desc;
//        Bitmap thumb = BitmapFactory.decodeResource(mContext.getResources(),
//                icon);
        msg.setThumbImage(bitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        mIWXAPI.sendReq(req);
    }

    public void pay2weixin(String partnerId, String prepayId, String noncestr, String timeStamp, String sign) {
        if (mIWXAPI == null) {
            getmIWXAPI();
        }
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(mContext, mContext.getString(R.string.weixin_not_install),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        PayReq request = new PayReq();
        request.appId = APPID_WEIXIN;
        request.partnerId = partnerId;
        request.prepayId= prepayId;
        request.packageValue = "Sign=WXPay";
        request.nonceStr= noncestr;
        request.timeStamp= timeStamp;
        request.sign= sign;
        mIWXAPI.sendReq(request);
    }
}
