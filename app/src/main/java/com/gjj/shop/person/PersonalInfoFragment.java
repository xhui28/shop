package com.gjj.shop.person;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gjj.applibrary.glide.GlideCircleTransform;
import com.gjj.applibrary.http.callback.StringDialogCallback;
import com.gjj.applibrary.log.L;
import com.gjj.applibrary.util.ToastUtil;
import com.gjj.shop.R;
import com.gjj.shop.base.BaseFragment;
import com.gjj.shop.base.PageSwitcher;
import com.gjj.shop.community.AddFeedFragment;
import com.gjj.shop.net.ApiConstants;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/14.
 */
public class PersonalInfoFragment extends BaseFragment {

    @Bind(R.id.avatar_item)
    RelativeLayout avatarItem;
    @Bind(R.id.name_item)
    RelativeLayout nameItem;
    @Bind(R.id.address_item)
    RelativeLayout addressItem;
    @Bind(R.id.change_psw_item)
    RelativeLayout changePswItem;
    @Bind(R.id.logout_btn)
    Button logoutBtn;
    @Bind(R.id.avatar)
    ImageView avatarIv;

    private PopupWindow mPickUpPopWindow;
    private  String DCIM = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_DCIM + "/Camera/";
    private  File rootDir = new File(DCIM);
    private String mPhotoUrl;

    @Override
    public int getContentViewLayout() {
        return R.layout.fragment_person_info;
    }

    @Override
    public void initView() {

    }

    @OnClick({R.id.avatar_item, R.id.name_item, R.id.address_item, R.id.change_psw_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar_item:
                showPickupWindow();
                break;
            case R.id.name_item:
                PageSwitcher.switchToTopNavPage(getActivity(),ChangeNameFragment.class,null,getString(R.string.change_name),getString(R.string.save));
                break;
            case R.id.address_item:
                break;
            case R.id.change_psw_item:
                PageSwitcher.switchToTopNavPage(getActivity(),ChangePswFragment.class,null,getString(R.string.change_psw),null);
                break;
        }
    }
    /**
     * 显示选择框
     */
    @SuppressWarnings("unused")
    private void showPickupWindow() {
        // dismissConstructNoticeWindow();
        View contentView;
        PopupWindow popupWindow = mPickUpPopWindow;
        if (popupWindow == null) {
            contentView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.choose_pic_pop, null);
            TextView takePhoto = (TextView) contentView.findViewById(R.id.take_photo);
            TextView takeGallery = (TextView) contentView.findViewById(R.id.take_gallery);
            TextView cancle = (TextView) contentView.findViewById(R.id.btn_cancel);
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doTakePhoto();
                    dismissConstructNoticeWindow();
                }
            });
            takeGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doPickPhotoFromGallery();
                    dismissConstructNoticeWindow();
                }
            });
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissConstructNoticeWindow();
                }
            });
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissConstructNoticeWindow();
                }
            });
            popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, false);
            mPickUpPopWindow = popupWindow;
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            mPickUpPopWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

        } else {
            contentView = popupWindow.getContentView();
        }
        //判读window是否显示，消失了就执行动画
        if (!popupWindow.isShowing()) {
            Animation animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.effect_bg_show);
            contentView.startAnimation(animation2);
        }

        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        // mPopWindow.showAsDropDown(view, 0, 0);
    }
    /**
     * 取消工程消息弹出框
     *
     * @return
     */
    private void dismissConstructNoticeWindow() {
        PopupWindow pickUpPopWindow = mPickUpPopWindow;
        if (null != pickUpPopWindow && pickUpPopWindow.isShowing()) {
            pickUpPopWindow.dismiss();
        }
    }
    public void doPickPhotoFromGallery() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        getActivity().startActivityForResult(intent, AddFeedFragment.GET_PHOTO_CODE);
    }

    public void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = getPhotoName(System.currentTimeMillis() / 1000);
        File file = new File(rootDir, fileName);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        mPhotoUrl = file.getAbsolutePath();
        getActivity().startActivityForResult(intent, AddFeedFragment.GET_CAMERA_CODE);
    }
    /**
     * 照片保存名字
     *
     * @param rq
     * @return
     */
    private String getPhotoName(long rq) {
        return "IMG_" + rq + ".jpg";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getActivity() == null) {
            return;
        }
        if (resultCode != getActivity().RESULT_OK) {
            return;
        }
        if (requestCode == AddFeedFragment.GET_PHOTO_CODE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (selectedImage != null) {
                    sendPicByUri(selectedImage);
                }
            }
        } else if (requestCode == AddFeedFragment.GET_CAMERA_CODE) {
//            if(!mList.contains(mPhotoUrl))
//                mList.add(mPhotoUrl);
//            mAdapter.notifyDataSetChanged();
            setAvatar();
        }

    }

    /**
     * 根据图库图片uri发送图片
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;
            if (picturePath == null || picturePath.equals("null")) {
                return;
            }
            mPhotoUrl = picturePath;
            setAvatar();
//            if(!mList.contains(picturePath))
//                mList.add(picturePath);
//            mAdapter.notifyDataSetChanged();
        }

    }


    void setAvatar() {
        Glide.with(getActivity())
                .load(mPhotoUrl)
                .centerCrop()
                .placeholder(R.mipmap.all_img_dot_pr)
                .error(R.mipmap.all_img_dot_pr)
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .into(avatarIv);
        HashMap<String, String> params = new HashMap<>();
        params.put("nickname", "骂老子");
        List<File> fileList = new ArrayList<>();
        fileList.add(new File(mPhotoUrl));

        OkHttpUtils.post(ApiConstants.UPDATE_USER_INFO)//
                .tag(this)//
                .cacheMode(CacheMode.NO_CACHE)
                .params(params)
                .addFileParams("avatar", fileList)
                .execute(new StringDialogCallback(getActivity()) {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                        ToastUtil.shortToast(R.string.commit_success);
//                        onBackPressed();
                    }
                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        if(response != null)
                            L.d("@@@@@>>", response.code());
                        ToastUtil.shortToast(R.string.fail);

                    }

                });
    }
}