package com.yunbao.live.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.yunbao.live.R;
import com.yunbao.live.bean.LiveReadyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市适配器
 * 
 * @author jayqiu
 * 
 */
public class GridTextAdapter extends BaseAdapter {

	private List<LiveReadyBean.GoodsBean> child_text_array = new ArrayList<LiveReadyBean.GoodsBean>();
	private Context mContext;

	public GridTextAdapter(Context context, List<LiveReadyBean.GoodsBean> child_text_array) {
		this.mContext = context;
		this.child_text_array = child_text_array;
	}

	@Override
	public int getCount() {
		return child_text_array.size();
	}

	@Override
	public Object getItem(int position) {
		return child_text_array.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler viewHodler = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_gridview_txt, null);
			viewHodler = new ViewHodler();
			viewHodler.mTvType = (TextView) convertView
					.findViewById(R.id.tv_item_goods_type);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHodler) convertView.getTag();
		}

		Log.i("-=======-", child_text_array.toString());

		viewHodler.mTvType.setText(child_text_array.get(position).getName());
		return convertView;
	}

	private class ViewHodler {
		private TextView mTvType;
	}

}
