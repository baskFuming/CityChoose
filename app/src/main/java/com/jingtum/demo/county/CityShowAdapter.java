package com.jingtum.demo.county;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingtum.demo.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yalei on 2016/10/26.
 */

public class CityShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<City> mCities;
    /**
     * 存放拼音首字母和下标的Map集合
     */
    private HashMap<String, Integer> letterIndexes;

    private OnCityClickListener mOnCityClickListener;

    private static final int VIEW_TYPE_THREAD = 333;

    public CityShowAdapter(Context context, @NonNull List<City> cities) {
        mContext = context;
        mCities = cities;
        mInflater = LayoutInflater.from(mContext);
        letterIndexes = new HashMap<>();

        //方便于右边的字母选择点击然后RecyclerView跳转
        for (int i = 0; i < mCities.size(); i++) {
            //当前城市拼音首字母
            String currentLetter = Utils.getFirstLetter(mCities.get(i).getPinyin());
            //上个首字母，如果不存在设为""
            String previousLetter = i >= 1 ? Utils.getFirstLetter(mCities.get(i - 1).getPinyin()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, i);
            }
        }
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

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        view=mInflater.inflate(R.layout.item_city_listview,parent,false);
        return new CityShowHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final String city = mCities.get(position).getName();
        ((CityShowHolder)holder).tv_item_city_listview_name.setText(city);
        final String area = mCities.get(position).getArea();
        ((CityShowHolder)holder).tv_item_city_listview_area.setText(area);
        String currentLetter = Utils.getFirstLetter(mCities.get(position).getPinyin());
        String previousLetter = position >= 1 ? Utils.getFirstLetter(mCities.get(position - 1).getPinyin()) : "";
        if (!TextUtils.equals(currentLetter, previousLetter)){
            ((CityShowHolder)holder).tv_item_city_listview_letter.setVisibility(View.VISIBLE);
            ((CityShowHolder)holder).tv_item_city_listview_letter.setText(currentLetter);
        }else{
            ((CityShowHolder)holder).tv_item_city_listview_letter.setVisibility(View.GONE);
        }
        ((CityShowHolder)holder).tv_item_city_listview_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCityClickListener != null){
                    mOnCityClickListener.onCityClick(mCities.get(position));
                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {

        return VIEW_TYPE_THREAD;
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }



    static class CityShowHolder extends RecyclerView.ViewHolder {
        TextView tv_item_city_listview_letter;
        TextView tv_item_city_listview_name;
        TextView tv_item_city_listview_area;
        public CityShowHolder(View itemView) {
            super(itemView);
            tv_item_city_listview_name= (TextView) itemView.findViewById(R.id.tv_item_city_listview_name);
            tv_item_city_listview_area= (TextView) itemView.findViewById(R.id.tv_item_city_listview_area);

            tv_item_city_listview_letter= (TextView) itemView.findViewById(R.id.tv_item_city_listview_letter);
        }
    }



    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
        mOnCityClickListener = onCityClickListener;
    }
}