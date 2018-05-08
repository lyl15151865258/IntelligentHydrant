package cn.njmeter.intelligenthydrant.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.widget.ExtendedEditText;
import cn.njmeter.intelligenthydrant.utils.StringUtils;

/**
 * 控件辅助工具
 * Created by LiYuliang on 2017/10/20.
 *
 * @author LiYuliang
 * @version 2018/03/15
 */

public class ViewUtils {

    private ExtendedEditText extendedEditText1, extendedEditText2;

    /**
     * 改变密码框可见性
     *
     * @param isInvisible 原先密码框可见性
     * @param editText    密码输入框
     * @param imageView   切换显示/隐藏图标
     */
    public static void changePasswordState(Boolean isInvisible, EditText editText, ImageView imageView) {
        if (isInvisible) {
            imageView.setImageResource(R.drawable.visible_blue);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            //切换后将EditText光标置于末尾
            CharSequence charSequence = editText.getText();
            if (charSequence != null) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        } else {
            imageView.setImageResource(R.drawable.invisible_gray);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            //切换后将EditText光标置于末尾
            setCharSequence(editText);
        }
    }

    /**
     * 将EditText光标置于末尾
     *
     * @param editText EditText控件
     */
    public static void setCharSequence(EditText editText) {
        CharSequence charSequence = editText.getText();
        editText.setSelection(charSequence.length());
    }

    /**
     * 改变日期（加减一天或一个月），修改日期显示并刷新数据
     *
     * @param mode  模式（年、月、日）
     * @param value 负数向前/正数向后
     */
    public static void changeDate(Context context, TextView tvDate, int mode, int value) {
        String date = tvDate.getText().toString().substring(0, 10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (mode) {
            case 0:
                //当前时间加上value年，负数为向前value年
                calendar.add(Calendar.YEAR, value);
                break;
            case 1:
                //当前时间加上value月，负数为向前value月
                calendar.add(Calendar.MONTH, value);
                break;
            case 2:
                //当前时间加上value星期，负数为向前value星期
                calendar.add(Calendar.WEEK_OF_YEAR, value);
                break;
            case 3:
                //当前时间加上value天，负数为向前value天
                calendar.add(Calendar.DAY_OF_YEAR, value);
                break;
            default:
                break;
        }
        calendar.getTime();
        //如果获得的时间大于当前时间则保持日期不变（主要针对向后的日期）
        Date currentDate = new Date();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(currentDate);
        //利用compareTo比较两者，大于返回1，小于返回-1，等于返回0
        if (calendar.compareTo(calendar1) != 1) {
            String newDate = DateFormat.format("yyyy-MM-dd", calendar).toString();
            tvDate.setText(String.format(context.getString(R.string.date), newDate));
        }
    }

    /**
     * 弹出日期选择框Dialog并设置日期和星期
     *
     * @param tv 文本控件
     */
    public static void selectData(Context context, TextView tv) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dataPickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
            tv.setText(String.format(context.getString(R.string.date), date));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = dataPickerDialog.getDatePicker();
        datePicker.setMaxDate(new Date(System.currentTimeMillis()).getTime());
        dataPickerDialog.show();
    }
}
