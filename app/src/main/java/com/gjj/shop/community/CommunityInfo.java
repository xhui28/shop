package com.gjj.shop.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chuck on 2016/7/27.
 */
public class CommunityInfo implements Serializable {

    private static final long serialVersionUID = -3143882360259832554L;
    public String thumbAvatar;
    public String nickname;
    public String details;
    public long createTime;
    public List<String> imageList;
    public List<String> thumbList;

}
