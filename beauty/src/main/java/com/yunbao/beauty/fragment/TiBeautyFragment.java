package com.yunbao.beauty.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yunbao.beauty.R;
import com.yunbao.beauty.adapter.TiBeautyAdapter;
import com.yunbao.beauty.model.TiBeauty;

/**
 * Created by Anko on 2018/12/1.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBeautyFragment extends LazyFragment {

    private List<TiBeauty> beautyList = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        setContentView(R.layout.fragment_ti_recyclerview);

        beautyList.clear();
        beautyList.addAll(Arrays.asList(TiBeauty.values()));

        RecyclerView tiBeautyRV = (RecyclerView) findViewById(R.id.tiRecyclerView);
        TiBeautyAdapter beautyAdapter = new TiBeautyAdapter(beautyList);
        tiBeautyRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tiBeautyRV.setAdapter(beautyAdapter);
    }

}
