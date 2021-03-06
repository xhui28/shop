package com.gjj.shop.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.gjj.applibrary.http.callback.JsonCallback;
import com.gjj.applibrary.task.MainTaskExecutor;
import com.gjj.applibrary.util.ToastUtil;
import com.gjj.shop.base.BaseFragment;
import com.gjj.shop.R;
import com.gjj.shop.base.PageSwitcher;
import com.gjj.shop.category.ProductCategoryFragment;
import com.gjj.shop.index.foreign.CategoryData;
import com.gjj.shop.index.foreign.ViewPagerGoodListFragment;
import com.gjj.shop.model.ProductInfo;
import com.gjj.shop.net.ApiConstants;
import com.gjj.shop.search.SearchFragment;
import com.gjj.shop.widget.HorizontalListView;
import com.gjj.shop.widget.UnScrollableGridView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.finalteam.loadingviewfinal.PtrClassicFrameLayout;
import cn.finalteam.loadingviewfinal.PtrDefaultHandler;
import cn.finalteam.loadingviewfinal.PtrFrameLayout;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Chuck on 2016/7/21.
 */
public class IndexFragment extends BaseFragment {

    @Bind(R.id.store_house_ptr_frame)
    PtrClassicFrameLayout mPtrClassicFrameLayout;

    @Bind(R.id.convenientBanner)
    ConvenientBanner mBanner;

//    @Bind(R.id.advice_list)
//    RecyclerViewFinal mAdviceList;
    @Bind(R.id.product_grid)
    UnScrollableGridView mUnScrollableGridView;

    @Bind(R.id.shop_grid)
    UnScrollableGridView mShopGridView;

    @Bind(R.id.scrollView)
    ScrollView mScrollView;

//    @Bind(R.id.cheap_shop)
//    TextView mCheapShop;

    @Bind(R.id.category)
    ImageView mCategory;

    @Bind(R.id.search_btn)
    ImageView mSearchBtn;

    @Bind(R.id.tv_title)
    TextView mTitleTV;

    @Bind(R.id.shop_list)
    HorizontalListView mHorizontalListView;
    private int[] mIcons = {R.mipmap.nav01,R.mipmap.nav02,R.mipmap.nav03,R.mipmap.nav04,R.mipmap.nav05};

    @OnClick(R.id.search_btn)
    void search() {
        PageSwitcher.switchToTopNavPageNoTitle(getActivity(),SearchFragment.class,null,"","");
    }

    @OnClick(R.id.category)
    void category() {
        PageSwitcher.switchToTopNavPage(getActivity(),ProductCategoryFragment.class,null,getString(R.string.category),"");
    }
    private String[] mNames ;
    private List<ActivityInfo> mActivityInfo;
//    @OnClick(R.id.cheap_shop)
    void goCheapShop() {
          Bundle bundle = new Bundle();
        ArrayList<CategoryData> dataList = new ArrayList<>();
        dataList.addAll(mActivityInfo.get(0).data);
        bundle.putParcelableArrayList("data", dataList);
        bundle.putInt("firstCate", mActivityInfo.get(0).sortId);
        PageSwitcher.switchToTopNavPage(getActivity(),ViewPagerGoodListFragment.class,bundle,getString(R.string.cheap_shop),"");
//        PageSwitcher.switchToTopNavPage(getActivity(),CheapShopListFragment.class,null,getString(R.string.cheap_shop),"");
    }

//    @OnClick(R.id.foreign_shop)
    void goForeignShop() {
        Bundle bundle = new Bundle();
        ArrayList<CategoryData> dataList = new ArrayList<>();
        dataList.addAll(mActivityInfo.get(1).data);
        bundle.putParcelableArrayList("data", dataList);
        bundle.putInt("firstCate", mActivityInfo.get(1).sortId);
        PageSwitcher.switchToTopNavPage(getActivity(),ViewPagerGoodListFragment.class,bundle,getString(R.string.foreign_shop),"");
    }

//    @OnClick(R.id.factory_shop)
    void goFactoryShop() {
        Bundle bundle = new Bundle();
        ArrayList<CategoryData> dataList = new ArrayList<>();
        dataList.addAll(mActivityInfo.get(3).data);
        bundle.putParcelableArrayList("data", dataList);
        bundle.putInt("firstCate", mActivityInfo.get(3).sortId);
        PageSwitcher.switchToTopNavPage(getActivity(),ViewPagerGoodListFragment.class,bundle,getString(R.string.factory_shop),"");
    }

//    @OnClick(R.id.supermarket_shop)
    void goSuperMaketShop() {
        Bundle bundle = new Bundle();
        ArrayList<CategoryData> dataList = new ArrayList<>();
        dataList.addAll(mActivityInfo.get(2).data);
        bundle.putParcelableArrayList("data", dataList);
        bundle.putInt("firstCate", mActivityInfo.get(2).sortId);
        PageSwitcher.switchToTopNavPage(getActivity(),ViewPagerGoodListFragment.class,bundle,getString(R.string.supermarket_shop),"");
    }
    void goFoodShop() {
        Bundle bundle = new Bundle();
        ArrayList<CategoryData> dataList = new ArrayList<>();
        dataList.addAll(mActivityInfo.get(4).data);
        bundle.putParcelableArrayList("data", dataList);
        bundle.putInt("firstCate", mActivityInfo.get(4).sortId);
        PageSwitcher.switchToTopNavPage(getActivity(),ViewPagerGoodListFragment.class,bundle,getString(R.string.food_shop),"");
    }
    private AdviceProductAdapter mProductAdapter;
    private String[] images = {"http://img2.imgtn.bdimg.com/it/u=3093785514,1341050958&fm=21&gp=0.jpg",
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://d.3987.com/sqmy_131219/001.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };

    private HorizontalListViewAdapter mHorizontalListViewAdapter;
    private GridAdapter gridAdapter;
    private List<IndexData.BannerBean> mBannerData;
    @Override
    public int getContentViewLayout() {
        return R.layout.fragment_index;
    }

    @Override
    public void initView() {
        mTitleTV.setText(R.string.app_name);
        mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                  requestData();
            }
        });
        List<IndexData.BannerBean> stringList = new ArrayList<>();
        for (String image: images) {
            IndexData.BannerBean bannerBean = new IndexData.BannerBean();
            bannerBean.logo = image;
            stringList.add(bannerBean);
        }
        mBanner.setPages(
                new CBViewHolderCreator<NetworkImageHolderView>() {
                    @Override
                    public NetworkImageHolderView createHolder() {
                        return new NetworkImageHolderView();
                    }
                }, stringList)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
        mBanner.startTurning(5000);
        mBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                IndexData.BannerBean bannerBean = mBannerData.get(position);
                bundle.putInt("sortId", bannerBean.sortId);
                bundle.putInt("type", 2);
                PageSwitcher.switchToTopNavPage(getActivity(),ProductListFragment.class,bundle,bannerBean.title,"");
            }
        });
//        String[] titles = {"怀师", "南怀瑾军校", "闭关", "南怀瑾", "南公庄严照", "怀师法相"};
//        String[] imgs = {"", "", "", "", "", ""};
        mHorizontalListViewAdapter = new HorizontalListViewAdapter(getActivity(),new ArrayList<ShopInfo>());
        mHorizontalListView.setAdapter(mHorizontalListViewAdapter);
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mShopInfo",mHorizontalListViewAdapter.getItem(position));
                PageSwitcher.switchToTopNavPageNoTitle(getActivity(),ShopFragment.class, bundle,"","");
            }
        });
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
//                LinearLayoutManager.VERTICAL);
//        // 设置布局管理器
//        mAdviceList.setLayoutManager(staggeredGridLayoutManager);
        ArrayList<ProductInfo> list = new ArrayList<ProductInfo>();
//        for (int i = 0; i< 10; i++) {
//            ProductInfo productInfo = new ProductInfo();
//            productInfo.name = "的撒旦";
//            productInfo.logo = images[0];
//            list.add(productInfo);
//        }
        mNames = new String[5];
        mNames[0]  = getString(R.string.cheap_shop);
        mNames[1]  = getString(R.string.foreign_shop);
        mNames[2]  = getString(R.string.supermarket_shop);
        mNames[3]  = getString(R.string.factory_shop);
        mNames[4]  = getString(R.string.food_shop);
        ShopGridAdapter shopGridAdapter = new ShopGridAdapter(getActivity(), mNames,mIcons);
        mShopGridView.setAdapter(shopGridAdapter);
        mShopGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        goCheapShop();
                        break;
                    case 1:
                        goForeignShop();
                        break;
                    case 2:
                        goSuperMaketShop();
                        break;
                    case 3:
                        goFactoryShop();
                        break;
                    case 4:
                        goFoodShop();
                        break;
                }
            }
        });
//        AdviceProductAdapter productAdapter = new AdviceProductAdapter(getActivity(), list);
//        mProductAdapter = productAdapter;
//        mAdviceList.setItemAnimator(null);
//        mAdviceList.setAdapter(productAdapter);
        gridAdapter = new GridAdapter(getActivity(), list);
        mUnScrollableGridView.setAdapter(gridAdapter);
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
        mUnScrollableGridView.setFocusable(false);
        mUnScrollableGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtil.shortToast(view.getContext(), "product:"+position);
                PageSwitcher.switchToTopNavPageNoTitle(getActivity(),ProductDetailFragment.class,null,"","");
            }
        });
        MainTaskExecutor.scheduleTaskOnUiThread(500, new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollTo(0, 0);
            }
        });
        mPtrClassicFrameLayout.autoRefresh();
    }


    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        OkHttpUtils.get(ApiConstants.HOME_PAGE)
                .tag(this)
                .cacheMode(CacheMode.NO_CACHE)
                .params(params)
                .execute(new JsonCallback<IndexData>(IndexData.class) {

                    @Override
                    public void onSuccess(final IndexData indexData, Call call, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPtrClassicFrameLayout.refreshComplete();
                                mHorizontalListViewAdapter.setData(indexData.shop);
                                gridAdapter.setData(indexData.promotion);
                                mBanner.setPages(
                                        new CBViewHolderCreator<NetworkImageHolderView>() {
                                            @Override
                                            public NetworkImageHolderView createHolder() {
                                                return new NetworkImageHolderView();
                                            }
                                        }, indexData.banner);
                                mBannerData = indexData.banner;
                                mActivityInfo = indexData.tags;
                            }
                        });
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mPtrClassicFrameLayout.refreshComplete();
                                ToastUtil.shortToast(R.string.fail);
                            }
                        });
                    }
                });
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ButterKnife.bind(this, view);
    }


}
