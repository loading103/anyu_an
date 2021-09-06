package com.yunbao.live.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.live.R;
import com.yunbao.live.bean.ItemNode;
import com.yunbao.live.bean.LiveReadyBean;
import com.yunbao.live.custom.MyGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpandableGridAdapter extends BaseExpandableListAdapter implements
        OnItemClickListener {

	private final List<LiveReadyBean> list;
	private Context context;
	private MyGridView gridview;

	List<LiveReadyBean.GoodsBean> child_array;

	public ExpandableGridAdapter(Context context, List<LiveReadyBean> mList) {
		this.context = context;
		this.list = mList;
	}

	/**
	 * 获取一级标签总数
	 */
	@Override
	public int getGroupCount() {
		return list.size();
	}

	/**
	 * 获取一级标签下二级标签的总数
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// 这里返回1是为了让ExpandableListView只显示一个ChildView，否则在展开
		// ExpandableListView时会显示和ChildCount数量相同的GridView
		return 1;
	}

	/**
	 * 获取一级标签内容
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	/**
	 * 获取一级标签下二级标签的内容
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).getGoods().get(childPosition);
	}

	/**
	 * 获取一级标签的ID
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/**
	 * 获取二级标签的ID
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/**
	 * 指定位置相应的组视图
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/**
	 * 对一级标签进行设置
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
//		convertView = (RelativeLayout) RelativeLayout.inflate(context,
//				R.layout.item_gridview_group_layout, null);
//
//		TextView group_title = (TextView) convertView
//				.findViewById(R.id.group_title);
//		group_title.setText(list.get(groupPosition).getName());

		convertView = (RelativeLayout) RelativeLayout.inflate(context,
				R.layout.item_gridview_group_layout, null);

		ImageView iv = (ImageView) convertView.findViewById(R.id.thumb);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView des = (TextView) convertView.findViewById(R.id.des);

		ImgLoader.displayWithPlaceError(context,list.get(groupPosition).getThumb(), iv,R.mipmap.icon_app_bg,
				R.mipmap.icon_app_bg);
		name.setText(list.get(groupPosition).getName());
		des.setText(list.get(groupPosition).getDes());
		return convertView;
	}

	/**
	 * 对一级标签下的二级标签进行设置
	 */
	@Override
	public View getChildView(final int groupPosition, int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent) {
		convertView = (LinearLayout) LinearLayout.inflate(context,
				R.layout.item_grid_child_layout, null);
//		gridview = (MyGridView) convertView.findViewById(R.id.gridview);

		int size = list.get(groupPosition).getGoods().size();
		child_array = new ArrayList<LiveReadyBean.GoodsBean>();
		for (int i = 0; i < size; i++) {
			child_array.add(list.get(groupPosition).getGoods().get(i));
		}
//		gridview.setAdapter(new GridTextAdapter(context, child_array));
//		gridview.setOnItemClickListener(this);

		final LiveReadyBean gData = list.get(groupPosition);

		RecyclerView recyclerView = convertView.findViewById(R.id.recyclerView);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new GridLayoutManager(context,6));
		LiveGiftChildAdapter adapter=new LiveGiftChildAdapter();
		adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
			@Override
			public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
				for (int i = 0; i < adapter.getData().size(); i++) {
					if(i==position){
						((LiveReadyBean.GoodsBean)adapter.getData().get(i)).setCheck(true);
						SPUtils.getInstance().put("goods_id",((LiveReadyBean.GoodsBean)adapter.
								getData().get(i)).getId());
						SPUtils.getInstance().put("goods_name",((LiveReadyBean.GoodsBean)adapter.
								getData().get(i)).getName());
						SPUtils.getInstance().put("type_id",gData.getId());
						SPUtils.getInstance().put("type_name",gData.getName());
					}else {
						((LiveReadyBean.GoodsBean)adapter.getData().get(i)).setCheck(false);
					}
				}

				adapter.notifyDataSetChanged();
			}
		});
		recyclerView.setAdapter(adapter);
		adapter.addData(child_array);
		return convertView;
	}

	/**
	 * 当选择子节点的时候，调用该方法
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
		Toast.makeText(context, "当前选中的是:" + child_array.get(position),
				Toast.LENGTH_SHORT).show();
	}
}