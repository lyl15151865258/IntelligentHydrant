package cn.njmeter.intelligenthydrant.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.adapter.TagAdapter;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.adapter.WaterCompanyAdapter;
import cn.njmeter.intelligenthydrant.bean.WaterMeterLoginResult;
import cn.njmeter.intelligenthydrant.sqlite.DbHelper;
import cn.njmeter.intelligenthydrant.sqlite.table.Table;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.LogUtils;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.utils.ViewUtils;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import cn.njmeter.intelligenthydrant.widget.dialog.CommonWarningDialog;
import cn.njmeter.intelligenthydrant.widget.flowtaglayout.FlowTagLayout;
import cn.njmeter.intelligenthydrant.widget.flowtaglayout.OnTagClickListener;
import cn.njmeter.intelligenthydrant.widget.flowtaglayout.OnTagLongClickListener;

/**
 * 选择水司层级
 * Created by LiYuliang on 2017/3/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/26
 */

public class ChooseCompanyActivity extends BaseActivity {

    private List<WaterMeterLoginResult.Data> dataList, platformsNameList;
    private List<String> historyList = new ArrayList<>();
    private EditText etSearch;
    private WaterCompanyAdapter waterCompanyAdapter;
    private DbHelper mDbHelper;
    private FlowTagLayout ftlHistory;
    private TagAdapter<String> historyAdapter;
    private Context context;
    private String httpPort;
    private String tableName = Table.HydrantCompanySearchTable.TABLE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_company);
        context = this;
        MyToolbar toolbar = findViewById(R.id.myToolbar);
        toolbar.initToolBar(this, toolbar, "选择消火栓单位", R.drawable.back_white, onClickListener);
        httpPort = HydrantApplication.getInstance().getAccount().getHttp_Port_CS();
        JsonObject jsonObject = new JsonParser().parse(getIntent().getStringExtra(getString(R.string.waterCompanyName))).getAsJsonObject();
        //Gson直接解析成对象
        WaterMeterLoginResult waterMeterLoginResult = new Gson().fromJson(jsonObject, WaterMeterLoginResult.class);
        //对象中拿到集合
        dataList = waterMeterLoginResult.getData();
        platformsNameList = new ArrayList<>();
        platformsNameList.addAll(dataList);
        historyAdapter = new TagAdapter<>(this);
        findViewById(R.id.iv_deleteSearch).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteHistory).setOnClickListener(onClickListener);
        ftlHistory = findViewById(R.id.ftl_history);
        ftlHistory.setOnTagClickListener(onTagClickListener);
        ftlHistory.setOnTagLongClickListener(onTagLongClickListener);
        ListView listViewPlatform = findViewById(R.id.listView_platform);
        waterCompanyAdapter = new WaterCompanyAdapter(this, platformsNameList);
        listViewPlatform.setAdapter(waterCompanyAdapter);
        listViewPlatform.setOnItemClickListener(onItemClickListener);
        etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(textWatcher);
        mDbHelper = HydrantApplication.getInstance().getmDbHelper();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData();
    }

    private AdapterView.OnItemClickListener onItemClickListener = (parent, view, position, id) -> {
        switch (parent.getId()) {
            case R.id.listView_platform:
                WaterMeterLoginResult.Data data = (WaterMeterLoginResult.Data) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("hieId", data.getHie_id());
                intent.putExtra("loginId", data.getLogin_id());
                setResult(RESULT_OK, intent);
                if (!Constants.EMPTY.equals(etSearch.getText().toString()) && !hasData(etSearch.getText().toString())) {
                    insertData(etSearch.getText().toString());
                }
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String text = etSearch.getText().toString();
            platformsNameList.clear();
            for (int i = 0; i < dataList.size(); i++) {
                LogUtils.d(dataList.get(i).getSupplier());
                if (dataList.get(i).getSupplier().contains(text)) {
                    platformsNameList.add(dataList.get(i));
                }
            }
            waterCompanyAdapter.notifyDataSetChanged();
        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_deleteSearch:
                etSearch.setText("");
                break;
            case R.id.iv_deleteHistory:
                deleteAllData();
                break;
            default:
                break;
        }
    };

    /**
     * 插入数据
     *
     * @param tempName 输入框的文字
     */
    private void insertData(String tempName) {
        ContentValues values = new ContentValues();
        values.put("name", tempName);
        values.put("httpPort", httpPort);
        mDbHelper.insert(tableName, values);
    }

    /**
     * 查询所有并显示在GridView列表上
     */
    private void queryData() {
        Cursor cursor = mDbHelper.findList(tableName, null, "httpPort = ?", new String[]{httpPort}, null, null, "id desc");
        // 清空list
        historyList.clear();
        // 查询到的数据添加到list集合
        while (cursor.moveToNext()) {
            String history = cursor.getString(1);
            historyList.add(history);
        }
        ftlHistory.setAdapter(historyAdapter);
        historyAdapter.clearAndAddAll(historyList);
        historyAdapter.notifyDataSetChanged();
        if (historyList.size() == 0) {
            ftlHistory.setVisibility(View.GONE);
        } else {
            ftlHistory.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    /**
     * 标签点击监听
     */
    private OnTagClickListener onTagClickListener = (parent, view, position) -> {
        //获取到用户点击列表里的文字,并自动填充到搜索框内
        TextView textView = view.findViewById(R.id.tv_tag);
        etSearch.setText(textView.getText().toString());
        ViewUtils.setCharSequence(etSearch);
    };

    /**
     * 标签长按监听
     */
    private OnTagLongClickListener onTagLongClickListener = (parent, view, position) -> {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(context, getString(R.string.warning_delete_history));
        commonWarningDialog.setCancelable(false);
        commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                deleteOneData(((TextView) view.findViewById(R.id.tv_tag)).getText().toString());
                queryData();
            }

            @Override
            public void onCancelClick() {
            }
        });
        commonWarningDialog.show();
    };

    /**
     * 检查数据库中是否已经有该条记录
     *
     * @param tempName 输入框的文字
     * @return 是否已包含
     */
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = mDbHelper.findList(tableName, null, "httpPort = ? and name = ?", new String[]{httpPort, tempName}, null, null, null);
        boolean hasData = cursor.moveToNext();
        cursor.close();
        return hasData;
    }

    /**
     * 清空本平台某一条搜索记录
     */
    private void deleteOneData(String name) {
        mDbHelper.delete(tableName, "name = ?", new String[]{name});
    }

    /**
     * 清空本平台下保存的搜索记录
     */
    private void deleteAllData() {
        CommonWarningDialog commonWarningDialog = new CommonWarningDialog(context, getString(R.string.warning_delete_history_all));
        commonWarningDialog.setCancelable(false);
        commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                mDbHelper.delete(tableName, "httpPort = ?", new String[]{httpPort});
                queryData();
            }

            @Override
            public void onCancelClick() {
            }
        });
        commonWarningDialog.show();
    }

}
