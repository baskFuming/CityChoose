package com.jingtum.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingtum.demo.county.City;
import com.jingtum.demo.county.CitySearchAdapter;
import com.jingtum.demo.county.CityShowAdapter;
import com.jingtum.demo.county.OnCityClickListener;
import com.jingtum.demo.county.SideLetterBar;
import com.jingtum.demo.county.Utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CityActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "CountryActivity";
    private CityActivity self = this;

    public static final int CONUTRY_TO_REGISTER_FIRST = 10002;

    private List<City> mAllCities = new ArrayList<>();

    private CitySearchAdapter mCitySearchAdapter;
    private CityShowAdapter mCityShowAdapter;

    private RecyclerView recycler_all_city;
    private RecyclerView recycler_search_result;
    private EditText et_search;
    private ImageView iv_search_clear;

    private TextView tv_letter_overlay;
    private SideLetterBar side_letter_bar;
    private LinearLayout empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        initData();
        initView();
    }

    private void initView() {
        recycler_all_city= (RecyclerView) findViewById(R.id.recycler_all_city);
        recycler_search_result= (RecyclerView) findViewById(R.id.recycler_search_result);
        et_search= (EditText) findViewById(R.id.et_search);
        iv_search_clear= (ImageView) findViewById(R.id.iv_search_clear);
        tv_letter_overlay= (TextView) findViewById(R.id.tv_letter_overlay);
        side_letter_bar= (SideLetterBar) findViewById(R.id.side_letter_bar);
        empty_view= (LinearLayout) findViewById(R.id.empty_view);

        recycler_all_city.setLayoutManager(new LinearLayoutManager(this));
        recycler_search_result.setLayoutManager(new LinearLayoutManager(this));
        recycler_search_result.setAdapter(mCitySearchAdapter);

        side_letter_bar.setOverlay(tv_letter_overlay);
        side_letter_bar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityShowAdapter.getLetterPosition(letter);
                //recycler_all_city.smoothScrollToPosition(position);
                LinearLayoutManager llm = (LinearLayoutManager) recycler_all_city
                        .getLayoutManager();
                llm.scrollToPositionWithOffset(position, 0);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = getPhonetic(s.toString());
                if (TextUtils.isEmpty(keyword)) {
                    iv_search_clear.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                    recycler_search_result.setVisibility(View.GONE);
                } else {
                    iv_search_clear.setVisibility(View.VISIBLE);
                    recycler_search_result.setVisibility(View.VISIBLE);
                    List<City> result = searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        empty_view.setVisibility(View.VISIBLE);
                    } else {
                        empty_view.setVisibility(View.GONE);
                        mCitySearchAdapter.changeData(result);

                    }
                }
            }
        });
        recycler_all_city.setAdapter(mCityShowAdapter);

        iv_search_clear.setOnClickListener(this);
    }
    public List<City> searchCity(final String keyword){

        List<City> result = new ArrayList<>();
        for (City city:
                mAllCities) {
            if (city.getPinyin() != null && city.getName() != null) {
                if (city.getPinyin().contains(keyword)) {
                    result.add(city);
                }
            }
        }
        return result;
    }
    public List<City> getAllCities(){

        InputStream inputStream = getResources().openRawResource(R.raw.country_code);
        String areaInfo = Utils.getStringFromInputStream(inputStream);

        List<City> result = new ArrayList<>();
        City city;

        JSONArray myJsonArray;
        try
        {
            myJsonArray = new JSONArray(areaInfo);
            for(int i=0 ; i < myJsonArray.length() ;i++)
            {
                //获取每一个JsonObject对象
                JSONObject provinceObject = myJsonArray.getJSONObject(i);
                //获取省区域的值
//                String ProvinceName = provinceObject.getString("title");
//                Log.i("areaInfo", "省区域名称内容为："+ProvinceName);
                JSONArray cityArray = provinceObject.getJSONArray("group");
                for(int j=0 ; j < cityArray.length() ;j++){
                    //获取每一个JsonObject对象
                    JSONArray cityObject = cityArray.getJSONArray(j);
                    //获取省区域的值
                    String cityName = cityObject.getString(0);
                    String areaName = cityObject.getString(1);
                    String phoneticName = getPhonetic(cityName);
                    if (i==0){
                        city = new City(cityName, "0",areaName);
                    }else {
                        city = new City(cityName, phoneticName,areaName);
                    }
                    result.add(city);

                }
            }
            return result;

        }catch (JSONException e)
        {

            e.printStackTrace();
            return result;

        }
    }
    public String getPhonetic(String name) {
        if(name == null)
        {
            return "";
        }
        String pinyinName = "";

        try
        {
            char[] nameChar = name.toCharArray();
            HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            for (int i = 0; i < nameChar.length; i++)
            {
                if (nameChar[i] > 128)
                {
                    try
                    {
                        pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                                nameChar[i], outputFormat)[0].toString();
                    }
                    catch (BadHanyuPinyinOutputFormatCombination e)
                    {
                        Log.e(TAG, e + "");
                    }
                }
                else
                {
                    pinyinName += nameChar[i];
                }
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, e + "");
            pinyinName = "";
        }
        return pinyinName;
    }



    private void initData() {
        List<City> citys = getAllCities();
        mAllCities.clear();
        mAllCities.addAll(citys);

        Log.d(TAG, "initData: dkdsfksf   ===   " + mAllCities.size());
        mCityShowAdapter=new CityShowAdapter(this,mAllCities);
        mCitySearchAdapter=new CitySearchAdapter(this,citys);
        mCityShowAdapter.setOnCityClickListener(new OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                EventBus.getDefault().post(city);

                self.onBackPressed();
            }
        });

        mCitySearchAdapter.setOnCityClickListener(new OnCityClickListener() {
            @Override
            public void onCityClick(City city) {
                EventBus.getDefault().post(city);

                self.onBackPressed();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_search_clear:
                iv_search_clear.setVisibility(View.GONE);
                et_search.setText("");
                empty_view.setVisibility(View.GONE);
                recycler_search_result.setVisibility(View.GONE);
                break;
        }
    }

    public void onButtonLeftClicked(View view) {
        super.onBackPressed();
    }

    //This is to hide keyboard when off focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

//            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }
}
