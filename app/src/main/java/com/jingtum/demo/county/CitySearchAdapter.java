package com.jingtum.demo.county;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jingtum.demo.R;
import java.util.List;
/**
 * Created by yalei on 2016/10/26.
 */

public class CitySearchAdapter extends RecyclerView.Adapter<CitySearchAdapter.CitySearchHolder>{
    private Context mContext;
    private List<City> mCities;
    private LayoutInflater mInflater;
    private OnCityClickListener mOnCityClickListener;

    public CitySearchAdapter(Context mContext, @NonNull List<City> mLists){
        this.mContext=mContext;
        this.mCities=mLists;
        mInflater= LayoutInflater.from(mContext);
    }

    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        mOnCityClickListener = onCityClickListener;
    }

    @Override
    public CitySearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.item_search_result_listview,parent,false);
        return new CitySearchHolder(view);
    }

    @Override
    public void onBindViewHolder(CitySearchHolder holder, final int position) {
        holder.tv_item_result_listview_name.setText(mCities.get(position).getName());
        holder.tv_item_result_listview_area.setText(mCities.get(position).getArea());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mOnCityClickListener!=null){

                    mOnCityClickListener.onCityClick(mCities.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCities==null?0:mCities.size();
    }

    public void changeData(List<City> list){
        if (mCities == null){
            mCities = list;
        }else{
            mCities.clear();
            mCities.addAll(list);
        }
        notifyDataSetChanged();
    }

    static class CitySearchHolder extends RecyclerView.ViewHolder{
        private TextView tv_item_result_listview_name;
        private TextView tv_item_result_listview_area;
        public CitySearchHolder(View itemView) {
            super(itemView);
            tv_item_result_listview_area= (TextView) itemView.findViewById(R.id.tv_item_result_listview_area);
            tv_item_result_listview_name= (TextView) itemView.findViewById(R.id.tv_item_result_listview_name);
        }
    }
}
