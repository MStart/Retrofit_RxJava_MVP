package com.example.y.mvp.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.y.mvp.R;
import com.example.y.mvp.adapter.JokeTextAdapter;
import com.example.y.mvp.constant.Constant;
import com.example.y.mvp.mvp.Bean.JokeTextBean;
import com.example.y.mvp.mvp.presenter.BasePresenter;
import com.example.y.mvp.mvp.presenter.JokeTextPresenterImpl;
import com.example.y.mvp.mvp.view.BaseView;
import com.example.y.mvp.utils.UIUtils;
import com.example.y.mvp.widget.MyRecyclerView;

import java.util.LinkedList;
import java.util.List;

/**
 * by y on 2016/5/30.
 */
public class JokeTextFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        MyRecyclerView.LoadingData, BaseView.JokeTextView {

    private MyRecyclerView recyclerView;
    private SwipeRefreshLayout srfLayout;

    private boolean isPrepared;
    private boolean isLoad;
    private static boolean isNull = false;
    private static int page = 1;

    private View inflate;

    private BasePresenter.JokeTextPresenter jokePresenter;
    private JokeTextAdapter adapter;


    @Override
    protected View initView() {
        if (inflate == null) {
            inflate = View.inflate(UIUtils.getActivity(), R.layout.fragment_joke_text, null);
            recyclerView = (MyRecyclerView) inflate.findViewById(R.id.recyclerView);
            srfLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.srf_layout);
            isPrepared = true;
        }
        return inflate;
    }

    @Override
    protected void initData() {

        if (!isPrepared || !isVisible || isLoad) {
            return;
        }

        jokePresenter = new JokeTextPresenterImpl(this);
        List<JokeTextBean.JokeTextInfo> jokeTextInfo = new LinkedList<>();

        srfLayout.setOnRefreshListener(this);

        adapter = new JokeTextAdapter(jokeTextInfo);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLoadingData(this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Constant.RECYCLERVIEW_LINEAR, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        srfLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
        isLoad = true;
    }

    @Override
    public void onRefresh() {
        adapter.removeAll();
        page = 1;
        jokePresenter.requestNetWork(page, isNull);
    }

    @Override
    public void onLoadMore() {
        if (!srfLayout.isRefreshing()) {
            ++page;
            jokePresenter.requestNetWork(page, isNull);
        }
    }

    @Override
    public void setData(List<JokeTextBean.JokeTextInfo> datas) {
        if (datas.isEmpty()) {
            isNull = true;
        } else {
            adapter.addAll(datas);
        }
    }

    @Override
    public void netWorkError() {
        Toast(UIUtils.getString(R.string.network_error));
    }

    @Override
    public void hideProgress() {
        srfLayout.setRefreshing(false);
    }

    @Override
    public void showProgress() {
        srfLayout.setRefreshing(true);
    }

    @Override
    public void showFoot() {
        adapter.isShowFooter(true);
    }

    @Override
    public void hideFoot() {
        adapter.isShowFooter(false);
    }

}
