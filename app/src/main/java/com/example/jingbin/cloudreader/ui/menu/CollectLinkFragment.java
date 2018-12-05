package com.example.jingbin.cloudreader.ui.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.jingbin.cloudreader.R;
import com.example.jingbin.cloudreader.adapter.CollectUrlAdapter;
import com.example.jingbin.cloudreader.base.BaseFragment;
import com.example.jingbin.cloudreader.bean.CollectUrlBean;
import com.example.jingbin.cloudreader.databinding.FragmentWanAndroidBinding;
import com.example.jingbin.cloudreader.utils.CommonUtils;
import com.example.jingbin.cloudreader.utils.ToastUtil;
import com.example.jingbin.cloudreader.viewmodel.wan.CollectLinkModel;
import com.example.jingbin.cloudreader.viewmodel.wan.WanNavigator;
import com.example.xrecyclerview.XRecyclerView;

import java.util.List;

import rx.Subscription;

/**
 * @author jingbin
 * @date 2018/09/27.
 * @description 网址
 */
public class CollectLinkFragment extends BaseFragment<FragmentWanAndroidBinding> implements WanNavigator.CollectUrlNavigator {

    private boolean mIsPrepared;
    private boolean mIsFirst = true;
    private FragmentActivity activity;
    private CollectUrlAdapter mAdapter;
    private CollectLinkModel viewModel;

    @Override
    public int setContent() {
        return R.layout.fragment_wan_android;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public static CollectLinkFragment newInstance() {
        return new CollectLinkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showContentView();
        initRefreshView();
        viewModel = new CollectLinkModel(this);

        // 准备就绪
        mIsPrepared = true;
        loadData();
    }


    private void initRefreshView() {
        bindingView.srlWan.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
        bindingView.xrvWan.setLayoutManager(new LinearLayoutManager(activity));
        bindingView.xrvWan.setPullRefreshEnabled(false);
        bindingView.xrvWan.clearHeader();
        mAdapter = new CollectUrlAdapter(activity);
        bindingView.xrvWan.setAdapter(mAdapter);
        bindingView.srlWan.setOnRefreshListener(() -> bindingView.srlWan.postDelayed(() -> {
            viewModel.getCollectUrlList();
        }, 300));
        bindingView.xrvWan.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                viewModel.getCollectUrlList();
            }
        });
    }

    @Override
    protected void loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return;
        }

        bindingView.srlWan.setRefreshing(true);
        bindingView.srlWan.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewModel.getCollectUrlList();
            }
        }, 150);
    }


    @Override
    protected void onRefresh() {
        bindingView.srlWan.setRefreshing(true);
        viewModel.getCollectUrlList();
    }

    @Override
    public void loadFailure() {
        showContentView();
        if (bindingView.srlWan.isRefreshing()) {
            bindingView.srlWan.setRefreshing(false);
        }
        bindingView.xrvWan.refreshComplete();
        ToastUtil.showToastLong("还没有收藏网址哦~");
    }

    @Override
    public void showAdapterView(CollectUrlBean bean) {
        List<CollectUrlBean.DataBean> data = bean.getData();
        mAdapter.clear();
        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
        bindingView.xrvWan.refreshComplete();

        if (data.size() > 10) {
            bindingView.xrvWan.noMoreLoading();
        }

        mIsFirst = false;
    }

    @Override
    public void showLoadSuccessView() {
        showContentView();
        bindingView.srlWan.setRefreshing(false);
    }

    @Override
    public void addRxSubscription(Subscription subscription) {
        addSubscription(subscription);
    }
}
