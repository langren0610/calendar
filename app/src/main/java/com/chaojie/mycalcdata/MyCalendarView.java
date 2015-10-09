package com.chaojie.mycalcdata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by chaojie on 2015/9/28.
 */
public class MyCalendarView extends LinearLayout implements View.OnTouchListener, View.OnClickListener {

    /**顶部年月显示**/
    private TextView textViewTop;
    private Context mContext;

    /**整个布局添加的主布局**/
    private LinearLayout linearLayoutMain;
    /**中间显示日期布局**/
    private LinearLayout linearLayoutChild;

    /**日期布局存储列表**/
    private List<ViewHolder> viewHolderList = new ArrayList<>();

    private float motionEventDownX;

    /**当前显示的月份**/
    private int currentMonth;
    /**当前显示的年份**/
    private int currentYear;

    /**农历时间工具**/
    private CalendarUtil calendarUtil;
    /**点击日期监听接口**/
    private ClickDateListener clickDateListener;
    /**今天时间背景**/
    private Drawable drawableCurrentDayBg;
    /**平常日期背景**/
    private Drawable drawableNormal;
    /**点击日期背景**/
    private Drawable drawableClickBg;
    /***上次点击的view**/
    private View lastClickView;
    /**画笔**/
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;

    private final int MARGIN_TOP = 20;
    private final int MARGIN_BOTTOM = 20;
    private final int MARGIN_LEFT = 30;
    private final int MARGIN_RIGHT = 30;

    private final int MARGIN_LITTLE = 5;

    private final int TEXT_TOP_SIZE = 18;
    private final int TEXT_DAY = 16;

    private final int ONE_WEEK = 7;
    private final int WEEKS = 6;

    private final float INTERVAL_X = 30;

    private final int TEXTVIEW_HEIGHT = 40;

    /***设置选中的日期年份**/
    private int selectYear = 0;
    /***设置选中的日期月份**/
    private int selectMOnth = 0;
    /***设置选中的日期天**/
    private int selectDay = 0;

    private final String LOG = MyCalendarView.class.getName();

    public MyCalendarView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public MyCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public MyCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    private void initCanvas() {
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(50, 192, 196));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        canvas.drawCircle(50, 50, 50, paint);
        drawableCurrentDayBg = new BitmapDrawable(bitmap);

        Bitmap bitmapNormal = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        drawableNormal = new BitmapDrawable(bitmapNormal);

        Bitmap bitmapClick = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        canvas.setBitmap(bitmapClick);
        canvas.drawCircle(50, 50, 40, paint);
        drawableClickBg = new BitmapDrawable(bitmapClick);
    }

    private void init(Context context) {
        calendarUtil = new CalendarUtil();

        /**设置主布局样式 start**/
        linearLayoutMain = new LinearLayout(mContext);
        LayoutParams layoutParamsMian = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearLayoutMain.setLayoutParams(layoutParamsMian);
        linearLayoutMain.setOrientation(VERTICAL);
        addView((linearLayoutMain));
        /**设置主布局样式 end**/

        Date date = new Date();

        /**设置顶部显示年月样式 start**/
        textViewTop = new TextView(context);
        textViewTop.setText(getTopTitle(date));
        textViewTop.setTextColor(getResources().getColor(android.R.color.black));
        LayoutParams layoutParamsTop = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsTop.setMargins(0, MARGIN_TOP, 0, 0);
        textViewTop.setGravity(Gravity.CENTER);
        textViewTop.setTextSize(TEXT_TOP_SIZE);
        textViewTop.setLayoutParams(layoutParamsTop);
        linearLayoutMain.addView(textViewTop);//将顶部显示的年月textview添加到主布局
        /**设置顶部显示年月样式 end**/

        /***设置当天日期样式 start**/
        LinearLayout linearLayout = new LinearLayout(mContext);
        LayoutParams layoutParamsCurrentDate = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TEXTVIEW_HEIGHT + 20);
        linearLayout.setLayoutParams(layoutParamsCurrentDate);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayoutMain.addView(linearLayout);

        String currentDateText = "今天:" + (date.getYear() + 1900) + "年" + (date.getMonth() + 1) + "月" + date.getDate() + "日";
        TextView textViewCurrentDate = new TextView(mContext);
        LayoutParams layoutParamsCurrentTextViewDate = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsCurrentTextViewDate.setMargins(5, 5, 5, 5);
        textViewCurrentDate.setLayoutParams(layoutParamsCurrentTextViewDate);
        textViewCurrentDate.setText(currentDateText);
        textViewCurrentDate.setTextSize(14);
        textViewCurrentDate.setGravity(Gravity.CENTER);
        linearLayout.addView(textViewCurrentDate);

        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date1 = new Date();
                initCalendarDays(date1.getYear(), date1.getMonth());
            }
        });
        /***设置当天日期样式 end**/

        /**设置顶部年月下面的横线样式 start**/
        TextView textViewa = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(0, MARGIN_TOP, 0, 0);
        textViewa.setLayoutParams(layoutParams);
        textViewa.setBackgroundColor(Color.LTGRAY);
        linearLayoutMain.addView(textViewa);//将横线添加到主布局
        /**设置顶部年月下面的横线样式 end**/

        /**设置显示星期栏样式 start ----------------------**/
        /**设置显示星期布局样式 start**/
        LinearLayout linearLayoutWeek = new LinearLayout(mContext);
        LayoutParams layoutParamsWeek = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayoutWeek.setLayoutParams(layoutParamsWeek);
        linearLayoutWeek.setOrientation(HORIZONTAL);
        /**设置显示星期布局样式 end**/

        /**设置星期样式 start**/
        Drawable drawable = new BitmapDrawable(bitmap);
        for (int i = 0; i < ONE_WEEK; ++i) {
            /**设置星期布局样式 start**/
            LinearLayout linearLayoutChild = new LinearLayout(mContext);
            LayoutParams layoutParamsChild = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParamsChild.weight = 1;
            linearLayoutChild.setOrientation(HORIZONTAL);
            linearLayoutChild.setLayoutParams(layoutParamsChild);
            /**设置星期布局样式 end**/

            /**设置显示星期的textView样式 start**/
            TextView textView = new TextView(context);
            LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams2.gravity = Gravity.CENTER;
            layoutParams2.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
            textView.setText(getWeekDay(i));
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(layoutParams2);
            /**设置显示星期的textView样式 end**/

            linearLayoutChild.addView(textView);//将显示星期的textview添加到相应的星期布局中
            linearLayoutWeek.addView(linearLayoutChild);//将星期布局添加到星期布局栏中
        }
        /**设置星期样式 start**/
        linearLayoutMain.addView(linearLayoutWeek);//将显示星期的布局添加到主布局中
        /**设置显示星期栏样式 end*---------------------------*/

        /**设置星期下面的横线样式 start**/
        TextView textView = new TextView(context);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(0, MARGIN_TOP, 0, 0);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(Color.LTGRAY);
        linearLayoutMain.addView(textView);
        /**设置星期下面的横线样式 end**/

        /**设置显示日期的主布局样式 start**/
        linearLayoutChild = new LinearLayout(mContext);
        LayoutParams layoutParamsChild = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linearLayoutChild.setOrientation(VERTICAL);
        linearLayoutChild.setLayoutParams(layoutParamsChild);
        linearLayoutMain.addView(linearLayoutChild);
        /**设置显示日期的布局样式 end**/

        initCanvas();//初始化画板画笔

        /**设置日期布局样式 start**/
        for (int i = 0; i < WEEKS; ++i) {
            /**设置一周日期栏主句样式 start**/
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.linearLayoutMain = new LinearLayout(mContext);
            LayoutParams layoutParams2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            viewHolder.linearLayoutMain.setOrientation(HORIZONTAL);
            viewHolder.linearLayoutMain.setLayoutParams(layoutParams2);
            viewHolder.viewHolderChildList = new ArrayList<>();

            linearLayoutChild.addView(viewHolder.linearLayoutMain);
            /**设置一周日期栏主句样式 end**/

            for (int j = 0; j < ONE_WEEK; ++j) {
                /**设置一个日期布局样式 start**/
                LayoutParams layoutParams3 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParams3.weight = 3;
                layoutParams3.setMargins(MARGIN_LEFT, MARGIN_LITTLE, MARGIN_RIGHT, 0);

                ViewHolderChild viewHolderChild = new ViewHolderChild();
                viewHolderChild.linearLayout = new LinearLayout(mContext);
                viewHolderChild.linearLayout.setOrientation(VERTICAL);
                viewHolderChild.linearLayout.setLayoutParams(layoutParams3);
                /**设置一个日期布局样式 end**/

                /**设置显示日期的textview start**/
                LayoutParams layoutParamsText = new LayoutParams(TEXTVIEW_HEIGHT, TEXTVIEW_HEIGHT);
                //layoutParamsText.weight = 9;

                viewHolderChild.textViewDay = new TextView(mContext);
                viewHolderChild.textViewDay.setText("");
                viewHolderChild.textViewDay.setTextSize(TEXT_DAY);
                viewHolderChild.textViewDay.setGravity(Gravity.CENTER);
                viewHolderChild.textViewDay.setLayoutParams(layoutParamsText);

                layoutParamsText = new LayoutParams(TEXTVIEW_HEIGHT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParamsText.setMargins(0, 10, 0, 0);
                //layoutParamsText.weight = 5;
                viewHolderChild.textViewLunar = new TextView(mContext);
                viewHolderChild.textViewLunar.setText("");
                viewHolderChild.textViewLunar.setTextSize(TEXT_DAY - 8);
                viewHolderChild.textViewLunar.setGravity(Gravity.CENTER);
                viewHolderChild.textViewLunar.setLayoutParams(layoutParamsText);
                /**设置显示日期的textview end**/

                viewHolderChild.linearLayout.addView(viewHolderChild.textViewDay);//将显示日期的textview添加到日期布局中
                viewHolderChild.linearLayout.addView(viewHolderChild.textViewLunar);//将显示农历日期的textview添加到日期布局中

                viewHolder.linearLayoutMain.addView(viewHolderChild.linearLayout);//将日期布局添加到一周日期栏布局中
                viewHolder.viewHolderChildList.add(viewHolderChild);//将一周日期栏布局添加到日期主布局中

                viewHolderChild.linearLayout.setId(i * ONE_WEEK + (j + 1));
                viewHolderChild.linearLayout.setOnClickListener(this);//设置点击日期监听事件
                //viewHolderChild.linearLayout.setOnTouchListener(this);
                viewHolder.linearLayoutMain.setOnTouchListener(this);
            }

            /**设置添加一条一周日期栏底部横线样式 start**/
            TextView textView1 = new TextView(context);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
            layoutParams.setMargins(0, MARGIN_TOP, 0, 0);
            textView1.setLayoutParams(layoutParams);
            textView1.setBackgroundColor(Color.LTGRAY);
            linearLayoutChild.addView(textView1);
            viewHolderList.add(viewHolder);
            /**设置添加一条一周日期栏底部横线样式 end**/
        }
        /**设置日期布局样式 start**/

        linearLayoutChild.setOnTouchListener(this);//设置监听显示日期主布局触摸事件
        initCalendarDays(date.getYear(), date.getMonth());//初始化显示当月的数据
    }

    private String getTopTitle(Date date) {
        String topTitle = (date.getYear() + 1900) + "年" + (date.getMonth() + 1) + "月";
        return topTitle;
    }

    /**
     * 根据index获取星期几
     * @param index
     * @return
     */
    private String getWeekDay(int index) {
        switch (index) {
            case 0:
                return "日";
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
        }
        return "";
    }

    /**
     * 根据时间获取一个月有多少天
     * @param date
     * @return
     */
    private int getMonthDays(Date date) {
        int days = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    /**
     * 初始化显示月的时间
     * @param year
     * @param month
     */
    public void initCalendarDays(int year, int month) {
        clear();
        Date todayDate = new Date();
        int today = todayDate.getDate();//当天的日期

        Date date = new Date();
        date.setYear(year);
        date.setMonth(month);
        date.setDate(1);

        String topTitle = getTopTitle(date);
        textViewTop.setText(topTitle);//显示当前的年月到顶部标题

        int days = getMonthDays(date);
        int day = date.getDay();
        int startIndex = day;
        int listIndex = 0;
        for (int i = 0; i < days; ++i) {
            if (startIndex > (ONE_WEEK - 1)) {
                startIndex = 0;
                listIndex++;
            }
            ViewHolder viewHolder = viewHolderList.get(listIndex);
            List<ViewHolderChild> viewHolderChildList = viewHolder.viewHolderChildList;
            ViewHolderChild viewHolderChild = viewHolderChildList.get(startIndex);

            int days1 = date.getDate();
            String daysStr = String.valueOf(days1);
            viewHolderChild.textViewDay.setText(daysStr);

            /***获取农历日期 start***/
            String chinesMonth = null;
            String lunar = calendarUtil.getChineseDay(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
            if (lunar.equals(calendarUtil.getChineseDay(0))) {
                chinesMonth = calendarUtil.getChineseMonth(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
            }
            if (chinesMonth != null && !chinesMonth.isEmpty()) {
                viewHolderChild.textViewLunar.setText(chinesMonth);
            } else {
                viewHolderChild.textViewLunar.setText(lunar);
            }
            /***获取农历日期 start***/

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateTime = simpleDateFormat.format(date);
            viewHolderChild.linearLayout.setTag(dateTime);

            if (days1 == today) {
                viewHolderChild.textViewDay.setBackgroundDrawable(drawableCurrentDayBg);
                viewHolderChild.textViewDay.setTextColor(Color.WHITE);
            } else {
                viewHolderChild.textViewDay.setBackgroundDrawable(drawableNormal);
                viewHolderChild.textViewDay.setTextColor(Color.BLACK);
            }

            if (selectYear == (date.getYear() + 1900) && selectMOnth == (date.getMonth() + 1) && selectDay == date.getDate() && today != selectDay) {
                viewHolderChild.textViewDay.setBackgroundDrawable(drawableClickBg);
                lastClickView = viewHolderChild.textViewDay;
            }

            /***获取二十四节气 start***/
            try {
                int month1 = date.getMonth();
                if (month1 == 0) {
                    month1 = 12;
                }
                calendarUtil.setGregorian(date.getYear() + 1900, month1, date.getDate());
                calendarUtil.computeChineseFields();
                calendarUtil.computeSolarTerms();
                String chilneseDate = calendarUtil.getDateString();
                if (chilneseDate != null && !chilneseDate.isEmpty()) {
                    viewHolderChild.textViewLunar.setText(chilneseDate);
                }
            } catch (Exception e) {
                Log.e(LOG, "get chinese date error!", e);
            }
            /***获取二十四节气 end***/
            days1 = days1 + 1;
            date.setDate(days1);

            startIndex++;
        }
        currentMonth = month;
        currentYear = year;
    }

    /**
     * 清除所有的日期
     */
    private void clear() {
        for (int i = 0; i < WEEKS; ++i) {
            ViewHolder viewHolder = viewHolderList.get(i);
            List<ViewHolderChild> viewHolderChildList = viewHolder.viewHolderChildList;
            for (int j = 0; j < ONE_WEEK; ++j) {
                ViewHolderChild viewHolderChild = viewHolderChildList.get(j);
                viewHolderChild.textViewDay.setText(" ");
                viewHolderChild.textViewLunar.setText(" ");
                viewHolderChild.linearLayout.setTag(null);
                viewHolderChild.textViewDay.setBackgroundDrawable(drawableNormal);
                viewHolderChild.textViewLunar.setTextColor(Color.BLACK);
                viewHolderChild.linearLayout.setBackgroundDrawable(drawableNormal);
            }
        }
        if (lastClickView != null) {
            lastClickView.setBackgroundDrawable(drawableNormal);
        }
    }

    /**
     * 设置选中的日期
     * @param year
     * @param month 1月到12月
     * @param day
     */
    public void setSelectDate(int year, int month, int day) {
        selectYear = year;
        selectMOnth = month;
        selectDay = day;
        currentYear = year - 1900;
        currentMonth = month - 1;
        initCalendarDays(currentYear, currentMonth);

        Date date = new Date();
        date.setMonth(month - 1);
        date.setDate(day);
        date.setYear(year - 1900);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = simpleDateFormat.format(date);
        if (clickDateListener != null) {
            clickDateListener.clickDate(dateTime);
        }
    }

    /**设置点击日期监听接口**/
    public void setOnClickDateListener(ClickDateListener clickDateListener) {
        this.clickDateListener = clickDateListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            motionEventDownX = event.getX();
        } else if (action == MotionEvent.ACTION_UP) {
            float motionEventUpX = event.getX();
            if ((motionEventUpX - motionEventDownX) >= INTERVAL_X) {//turn righr
                if (currentMonth <= 0) {//如果当前显示的一经是一月份,则将当前月份置为11月,年份减一年
                    currentMonth = 11;
                    currentYear = currentYear - 1;
                } else {//如果当前显示的月份不是一月，则显示上一个月份日期
                    currentMonth = currentMonth - 1;
                }
                initCalendarDays(currentYear, currentMonth);
            } else if ((motionEventDownX - motionEventUpX) >= INTERVAL_X) {//turn left
                if (currentMonth >= 11) {//如果当前显示的一经是十二月份,则将当前月份置为1月,年份加一年
                    currentMonth = 0;
                    currentYear = currentYear + 1;
                } else {//如果当前显示的月份不是十二月，则显示下一个月份日期
                    currentMonth = currentMonth + 1;
                }
                initCalendarDays(currentYear, currentMonth);
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String formatterTime = (String) v.getTag();
        if (formatterTime != null && clickDateListener != null) {
            if (lastClickView != null) {
                lastClickView.setBackgroundDrawable(drawableNormal);
            }
            TextView textView =  (TextView)((ViewGroup) v).getChildAt(0);
            textView.setBackgroundDrawable(drawableClickBg);
            lastClickView = textView;
            clickDateListener.clickDate(formatterTime);
        }
    }

    private class ViewHolder {
        public LinearLayout linearLayoutMain;
        public List<ViewHolderChild> viewHolderChildList;
    }

    private class ViewHolderChild {
        public LinearLayout linearLayout;
        public TextView textViewDay;
        public TextView textViewLunar;
    }

    /**
     * 点击日期监听接口
     */
    public interface ClickDateListener {
        /**
         * yyyy-MM-dd HH:mm:ss
         * @param formatterTime
         */
        public void clickDate(String formatterTime);
    }

    private class CalendarUtil {
        private int gregorianYear;
        private int gregorianMonth;
        private int gregorianDate;
        private boolean isGregorianLeap;
        private int dayOfYear;
        private int dayOfWeek; // 周日一星期的第一天
        private int chineseYear;
        private int chineseMonth; // 负数表示闰月
        private int chineseDate;
        private int sectionalTerm;
        private int principleTerm;
        private char[] daysInGregorianMonth = { 31, 28, 31, 30, 31, 30, 31,
                31, 30, 31, 30, 31 };
        private String[] stemNames = { "甲", "乙", "丙", "丁", "戊", "己", "庚",
                "辛", "壬", "癸" };
        private String[] branchNames = { "子", "丑", "寅", "卯", "辰", "巳", "午",
                "未", "申", "酉", "戌", "亥" };
        private String[] animalNames = { "鼠", "牛", "虎", "兔", "龙", "蛇", "马",
                "羊", "猴", "鸡", "狗", "猪" };

        public final String[] daysOfMonth = { "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17",
                "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
                "29", "30", "31" };

        private String monthOfAlmanac[] = {"正月","二月","三月","四月","五月","六月","七月","八月","九月","十月","冬月","腊月"};
        private String daysOfAlmanac[] = { "初一", "初二", "初三", "初四", "初五", "初六",
                "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七",
                "十八", "十九", "廿", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八",
                "廿九", "卅" }; // 农历的天数

        public CalendarUtil() {
            setGregorian(1901, 1, 1);
        }

        /**
         * 得到对应天的农历 要判断闰月 月初 月末         *
         * @param y
         * @param m
         * @param d
         * @return String
         */
        public String getChineseDay(int y, int m, int d) {
            CalendarUtil c = new CalendarUtil();
            c.setGregorian(y, m, d);
            c.computeChineseFields();
            c.computeSolarTerms();
            int cd = c.getChineseDate();
            return daysOfAlmanac[cd - 1];
        }

        /**
         * 获取农历的某一天
         * @param index
         * @return
         */
        public String getChineseDay(int index) {
            return daysOfAlmanac[index];
        }

        /**
         *  得到对应天的农历
         *  要判断闰月 月初 月末
         * @param y
         * @param m
         * @param d
         * @return
         */
        public String getChineseMonth(int y, int m, int d) {
            setGregorian(y,m,d);
            computeChineseFields();
            computeSolarTerms();

            int cd = getChineseMonth();
            if(cd < 1 || cd > 29)
                cd = 1;
            return monthOfAlmanac[cd -1];
        }

        /**
         * 获取农历月份
         * @param chineseMonth
         * @return
         */
        public int getChineseMonth(String chineseMonth) {
            int month = -1;
            if (chineseMonth.equals(monthOfAlmanac[0])) {
                month = 1;
            }
            if (chineseMonth.equals(monthOfAlmanac[1])) {
                month = 2;
            }
            if (chineseMonth.equals(monthOfAlmanac[2])) {
                month = 3;
            }
            if (chineseMonth.equals(monthOfAlmanac[3])) {
                month = 4;
            }
            if (chineseMonth.equals(monthOfAlmanac[4])) {
                month = 5;
            }
            if (chineseMonth.equals(monthOfAlmanac[5])) {
                month = 6;
            }
            if (chineseMonth.equals(monthOfAlmanac[6])) {
                month = 7;
            }
            if (chineseMonth.equals(monthOfAlmanac[7])) {
                month = 8;
            }
            if (chineseMonth.equals(monthOfAlmanac[8])) {
                month = 9;
            }
            if (chineseMonth.equals(monthOfAlmanac[9])) {
                month = 10;
            }
            if (chineseMonth.equals(monthOfAlmanac[10])) {
                month = 11;
            }
            if (chineseMonth.equals(monthOfAlmanac[11])) {
                month = 12;
            }
            return month;
        }

        public void setGregorian(int y, int m, int d) {
            gregorianYear = y;
            gregorianMonth = m;
            gregorianDate = d;
            isGregorianLeap = isGregorianLeapYear(y);
            dayOfYear = dayOfYear(y, m, d);
            dayOfWeek = dayOfWeek(y, m, d);
            chineseYear = 0;
            chineseMonth = 0;
            chineseDate = 0;
            sectionalTerm = 0;
            principleTerm = 0;
        }

        // 判断是否是闰年
        public boolean isGregorianLeapYear(int year) {
            boolean isLeap = false;
            if (year % 4 == 0)
                isLeap = true;
            if (year % 100 == 0)
                isLeap = false;
            if (year % 400 == 0)
                isLeap = true;
            return isLeap;
        }

        // 返回一个月有几天
        public int daysInGregorianMonth(int y, int m) {
            int d = daysInGregorianMonth[m - 1];
            if (m == 2 && isGregorianLeapYear(y))
                d++; // 公历闰年二月多一天
            return d;
        }

        // 计算当前天在本年中是第几天
        public int dayOfYear(int y, int m, int d) {
            int c = 0;
            for (int i = 1; i < m; i++) {
                c = c + daysInGregorianMonth(y, i);
            }
            c = c + d;
            return c;
        }

        // 当前天是本周的第几天 ， 从星期天开始算
        public int dayOfWeek(int y, int m, int d) {
            int w = 1; // 公历一年一月一日是星期一，所以起始值为星期日
            y = (y - 1) % 400 + 1; // 公历星期值分部 400 年循环一次
            int ly = (y - 1) / 4; // 闰年次数
            ly = ly - (y - 1) / 100;
            ly = ly + (y - 1) / 400;
            int ry = y - 1 - ly; // 常年次数
            w = w + ry; // 常年星期值增一
            w = w + 2 * ly; // 闰年星期值增二
            w = w + dayOfYear(y, m, d);
            w = (w - 1) % 7 + 1;
            return w;
        }

        // 农历月份大小压缩表，两个字节表示一年。两个字节共十六个二进制位数，
        // 前四个位数表示闰月月份，后十二个位数表示十二个农历月份的大小。
        private char[] chineseMonths = { 0x00, 0x04, 0xad, 0x08, 0x5a, 0x01,
                0xd5, 0x54, 0xb4, 0x09, 0x64, 0x05, 0x59, 0x45, 0x95, 0x0a, 0xa6,
                0x04, 0x55, 0x24, 0xad, 0x08, 0x5a, 0x62, 0xda, 0x04, 0xb4, 0x05,
                0xb4, 0x55, 0x52, 0x0d, 0x94, 0x0a, 0x4a, 0x2a, 0x56, 0x02, 0x6d,
                0x71, 0x6d, 0x01, 0xda, 0x02, 0xd2, 0x52, 0xa9, 0x05, 0x49, 0x0d,
                0x2a, 0x45, 0x2b, 0x09, 0x56, 0x01, 0xb5, 0x20, 0x6d, 0x01, 0x59,
                0x69, 0xd4, 0x0a, 0xa8, 0x05, 0xa9, 0x56, 0xa5, 0x04, 0x2b, 0x09,
                0x9e, 0x38, 0xb6, 0x08, 0xec, 0x74, 0x6c, 0x05, 0xd4, 0x0a, 0xe4,
                0x6a, 0x52, 0x05, 0x95, 0x0a, 0x5a, 0x42, 0x5b, 0x04, 0xb6, 0x04,
                0xb4, 0x22, 0x6a, 0x05, 0x52, 0x75, 0xc9, 0x0a, 0x52, 0x05, 0x35,
                0x55, 0x4d, 0x0a, 0x5a, 0x02, 0x5d, 0x31, 0xb5, 0x02, 0x6a, 0x8a,
                0x68, 0x05, 0xa9, 0x0a, 0x8a, 0x6a, 0x2a, 0x05, 0x2d, 0x09, 0xaa,
                0x48, 0x5a, 0x01, 0xb5, 0x09, 0xb0, 0x39, 0x64, 0x05, 0x25, 0x75,
                0x95, 0x0a, 0x96, 0x04, 0x4d, 0x54, 0xad, 0x04, 0xda, 0x04, 0xd4,
                0x44, 0xb4, 0x05, 0x54, 0x85, 0x52, 0x0d, 0x92, 0x0a, 0x56, 0x6a,
                0x56, 0x02, 0x6d, 0x02, 0x6a, 0x41, 0xda, 0x02, 0xb2, 0xa1, 0xa9,
                0x05, 0x49, 0x0d, 0x0a, 0x6d, 0x2a, 0x09, 0x56, 0x01, 0xad, 0x50,
                0x6d, 0x01, 0xd9, 0x02, 0xd1, 0x3a, 0xa8, 0x05, 0x29, 0x85, 0xa5,
                0x0c, 0x2a, 0x09, 0x96, 0x54, 0xb6, 0x08, 0x6c, 0x09, 0x64, 0x45,
                0xd4, 0x0a, 0xa4, 0x05, 0x51, 0x25, 0x95, 0x0a, 0x2a, 0x72, 0x5b,
                0x04, 0xb6, 0x04, 0xac, 0x52, 0x6a, 0x05, 0xd2, 0x0a, 0xa2, 0x4a,
                0x4a, 0x05, 0x55, 0x94, 0x2d, 0x0a, 0x5a, 0x02, 0x75, 0x61, 0xb5,
                0x02, 0x6a, 0x03, 0x61, 0x45, 0xa9, 0x0a, 0x4a, 0x05, 0x25, 0x25,
                0x2d, 0x09, 0x9a, 0x68, 0xda, 0x08, 0xb4, 0x09, 0xa8, 0x59, 0x54,
                0x03, 0xa5, 0x0a, 0x91, 0x3a, 0x96, 0x04, 0xad, 0xb0, 0xad, 0x04,
                0xda, 0x04, 0xf4, 0x62, 0xb4, 0x05, 0x54, 0x0b, 0x44, 0x5d, 0x52,
                0x0a, 0x95, 0x04, 0x55, 0x22, 0x6d, 0x02, 0x5a, 0x71, 0xda, 0x02,
                0xaa, 0x05, 0xb2, 0x55, 0x49, 0x0b, 0x4a, 0x0a, 0x2d, 0x39, 0x36,
                0x01, 0x6d, 0x80, 0x6d, 0x01, 0xd9, 0x02, 0xe9, 0x6a, 0xa8, 0x05,
                0x29, 0x0b, 0x9a, 0x4c, 0xaa, 0x08, 0xb6, 0x08, 0xb4, 0x38, 0x6c,
                0x09, 0x54, 0x75, 0xd4, 0x0a, 0xa4, 0x05, 0x45, 0x55, 0x95, 0x0a,
                0x9a, 0x04, 0x55, 0x44, 0xb5, 0x04, 0x6a, 0x82, 0x6a, 0x05, 0xd2,
                0x0a, 0x92, 0x6a, 0x4a, 0x05, 0x55, 0x0a, 0x2a, 0x4a, 0x5a, 0x02,
                0xb5, 0x02, 0xb2, 0x31, 0x69, 0x03, 0x31, 0x73, 0xa9, 0x0a, 0x4a,
                0x05, 0x2d, 0x55, 0x2d, 0x09, 0x5a, 0x01, 0xd5, 0x48, 0xb4, 0x09,
                0x68, 0x89, 0x54, 0x0b, 0xa4, 0x0a, 0xa5, 0x6a, 0x95, 0x04, 0xad,
                0x08, 0x6a, 0x44, 0xda, 0x04, 0x74, 0x05, 0xb0, 0x25, 0x54, 0x03 };


        // 初始日，公历农历对应日期：
        // 公历 1901 年 1 月 1 日，对应农历 4598 年 11 月 11 日
        private int baseYear = 1901;
        private int baseMonth = 1;
        private int baseDate = 1;
        private int baseIndex = 0;
        private int baseChineseYear = 4598 - 1;
        private int baseChineseMonth = 11;
        private int baseChineseDate = 11;

        public int computeChineseFields() {
            if (gregorianYear < 1901 || gregorianYear > 2100)
                return 1;
            int startYear = baseYear;
            int startMonth = baseMonth;
            int startDate = baseDate;
            chineseYear = baseChineseYear;
            chineseMonth = baseChineseMonth;
            chineseDate = baseChineseDate;
            // 第二个对应日，用以提高计算效率
            // 公历 2000 年 1 月 1 日，对应农历 4697 年 11 月 25 日
            if (gregorianYear >= 2000) {
                startYear = baseYear + 99;
                startMonth = 1;
                startDate = 1;
                chineseYear = baseChineseYear + 99;
                chineseMonth = 11;
                chineseDate = 25;
            }
            int daysDiff = 0;
            for (int i = startYear; i < gregorianYear; i++) {
                daysDiff += 365;
                if (isGregorianLeapYear(i))
                    daysDiff += 1; // leap year
            }
            for (int i = startMonth; i < gregorianMonth; i++) {
                daysDiff += daysInGregorianMonth(gregorianYear, i);
            }
            daysDiff += gregorianDate - startDate;


            chineseDate += daysDiff;
            int lastDate = daysInChineseMonth(chineseYear, chineseMonth);
            int nextMonth = nextChineseMonth(chineseYear, chineseMonth);
            while (chineseDate > lastDate) {
                if (Math.abs(nextMonth) < Math.abs(chineseMonth))
                    chineseYear++;
                chineseMonth = nextMonth;
                chineseDate -= lastDate;
                lastDate = daysInChineseMonth(chineseYear, chineseMonth);
                nextMonth = nextChineseMonth(chineseYear, chineseMonth);
            }
            return 0;
        }

        private int[] bigLeapMonthYears = {
                // 大闰月的闰年年份
                6, 14, 19, 25, 33, 36, 38, 41, 44, 52, 55, 79, 117, 136, 147, 150,
                155, 158, 185, 193 };

        public int daysInChineseMonth(int y, int m) {
            // 注意：闰月 m < 0
            int index = y - baseChineseYear + baseIndex;
            int v = 0;
            int l = 0;
            int d = 30;
            if (1 <= m && m <= 8) {
                v = chineseMonths[2 * index];
                l = m - 1;
                if (((v >> l) & 0x01) == 1)
                    d = 29;
            } else if (9 <= m && m <= 12) {
                v = chineseMonths[2 * index + 1];
                l = m - 9;
                if (((v >> l) & 0x01) == 1)
                    d = 29;
            } else {
                v = chineseMonths[2 * index + 1];
                v = (v >> 4) & 0x0F;
                if (v != Math.abs(m)) {
                    d = 0;
                } else {
                    d = 29;
                    for (int i = 0; i < bigLeapMonthYears.length; i++) {
                        if (bigLeapMonthYears[i] == index) {
                            d = 30;
                            break;
                        }
                    }
                }
            }
            return d;
        }

        public int nextChineseMonth(int y, int m) {
            int n = Math.abs(m) + 1;
            if (m > 0) {
                int index = y - baseChineseYear + baseIndex;
                int v = chineseMonths[2 * index + 1];
                v = (v >> 4) & 0x0F;
                if (v == m)
                    n = -m;
            }
            if (n == 13)
                n = 1;
            return n;
        }

        private char[][] sectionalTermMap = {
                { 7, 6, 6, 6, 6, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 5, 5,
                        5, 5, 5, 4, 5, 5 },
                { 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 3,
                        3, 4, 4, 3, 3, 3 },
                { 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
                        5, 5, 4, 5, 5, 5, 5 },
                { 5, 5, 6, 6, 5, 5, 5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 4, 4, 5, 5, 4, 4,
                        4, 5, 4, 4, 4, 4, 5 },
                { 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5, 5, 6, 5, 5,
                        5, 5, 4, 5, 5, 5, 5 },
                { 6, 6, 7, 7, 6, 6, 6, 7, 6, 6, 6, 6, 5, 6, 6, 6, 5, 5, 6, 6, 5, 5,
                        5, 6, 5, 5, 5, 5, 4, 5, 5, 5, 5 },
                { 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
                        7, 7, 6, 6, 6, 7, 7 },
                { 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
                        7, 7, 6, 7, 7, 7, 6, 6, 7, 7, 7 },
                { 8, 8, 8, 9, 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7,
                        7, 7, 6, 7, 7, 7, 7 },
                { 9, 9, 9, 9, 8, 9, 9, 9, 8, 8, 9, 9, 8, 8, 8, 9, 8, 8, 8, 8, 7, 8,
                        8, 8, 7, 7, 8, 8, 8 },
                { 8, 8, 8, 8, 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7,
                        7, 7, 6, 6, 7, 7, 7 },
                { 7, 8, 8, 8, 7, 7, 8, 8, 7, 7, 7, 8, 7, 7, 7, 7, 6, 7, 7, 7, 6, 6,
                        7, 7, 6, 6, 6, 7, 7 } };
        private char[][] sectionalTermYear = {
                { 13, 49, 85, 117, 149, 185, 201, 250, 250 },
                { 13, 45, 81, 117, 149, 185, 201, 250, 250 },
                { 13, 48, 84, 112, 148, 184, 200, 201, 250 },
                { 13, 45, 76, 108, 140, 172, 200, 201, 250 },
                { 13, 44, 72, 104, 132, 168, 200, 201, 250 },
                { 5, 33, 68, 96, 124, 152, 188, 200, 201 },
                { 29, 57, 85, 120, 148, 176, 200, 201, 250 },
                { 13, 48, 76, 104, 132, 168, 196, 200, 201 },
                { 25, 60, 88, 120, 148, 184, 200, 201, 250 },
                { 16, 44, 76, 108, 144, 172, 200, 201, 250 },
                { 28, 60, 92, 124, 160, 192, 200, 201, 250 },
                { 17, 53, 85, 124, 156, 188, 200, 201, 250 } };
        private char[][] principleTermMap = {
                { 21, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20,
                        20, 20, 20, 20, 20, 19, 20, 20, 20, 19, 19, 20 },
                { 20, 19, 19, 20, 20, 19, 19, 19, 19, 19, 19, 19, 19, 18, 19, 19,
                        19, 18, 18, 19, 19, 18, 18, 18, 18, 18, 18, 18 },
                { 21, 21, 21, 22, 21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21,
                        20, 20, 20, 21, 20, 20, 20, 20, 19, 20, 20, 20, 20 },
                { 20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 20, 20, 20, 20,
                        19, 20, 20, 20, 19, 19, 20, 20, 19, 19, 19, 20, 20 },
                { 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21,
                        20, 21, 21, 21, 20, 20, 21, 21, 20, 20, 20, 21, 21 },
                { 22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 21, 21, 21, 22,
                        21, 21, 21, 21, 20, 21, 21, 21, 20, 20, 21, 21, 21 },
                { 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23, 22, 23, 23, 23,
                        22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 23 },
                { 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
                        22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
                { 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24, 23, 23, 23, 23,
                        22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23, 23 },
                { 24, 24, 24, 24, 23, 24, 24, 24, 23, 23, 24, 24, 23, 23, 23, 24,
                        23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 23 },
                { 23, 23, 23, 23, 22, 23, 23, 23, 22, 22, 23, 23, 22, 22, 22, 23,
                        22, 22, 22, 22, 21, 22, 22, 22, 21, 21, 22, 22, 22 },
                { 22, 22, 23, 23, 22, 22, 22, 23, 22, 22, 22, 22, 21, 22, 22, 22,
                        21, 21, 22, 22, 21, 21, 21, 22, 21, 21, 21, 21, 22 } };
        private char[][] principleTermYear = {
                { 13, 45, 81, 113, 149, 185, 201 },
                { 21, 57, 93, 125, 161, 193, 201 },
                { 21, 56, 88, 120, 152, 188, 200, 201 },
                { 21, 49, 81, 116, 144, 176, 200, 201 },
                { 17, 49, 77, 112, 140, 168, 200, 201 },
                { 28, 60, 88, 116, 148, 180, 200, 201 },
                { 25, 53, 84, 112, 144, 172, 200, 201 },
                { 29, 57, 89, 120, 148, 180, 200, 201 },
                { 17, 45, 73, 108, 140, 168, 200, 201 },
                { 28, 60, 92, 124, 160, 192, 200, 201 },
                { 16, 44, 80, 112, 148, 180, 200, 201 },
                { 17, 53, 88, 120, 156, 188, 200, 201 } };

        public int computeSolarTerms() {
            if (gregorianYear < 1901 || gregorianYear > 2100)
                return 1;
            sectionalTerm = sectionalTerm(gregorianYear, gregorianMonth);
            principleTerm = principleTerm(gregorianYear, gregorianMonth);
            return 0;
        }

        public int sectionalTerm(int y, int m) {
            if (y < 1901 || y > 2100)
                return 0;
            int index = 0;
            int ry = y - baseYear + 1;
            while (ry >= sectionalTermYear[m - 1][index])
                index++;
            int term = sectionalTermMap[m - 1][4 * index + ry % 4];
            if ((ry == 121) && (m == 4))
                term = 5;
            if ((ry == 132) && (m == 4))
                term = 5;
            if ((ry == 194) && (m == 6))
                term = 6;
            return term;
        }

        public int principleTerm(int y, int m) {
            if (y < 1901 || y > 2100)
                return 0;
            int index = 0;
            int ry = y - baseYear + 1;
            while (ry >= principleTermYear[m - 1][index])
                index++;
            int term = principleTermMap[m - 1][4 * index + ry % 4];
            if ((ry == 171) && (m == 3))
                term = 21;
            if ((ry == 181) && (m == 5))
                term = 21;
            return term;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append("Gregorian Year: " + gregorianYear + "\n");
            buf.append("Gregorian Month: " + gregorianMonth + "\n");
            buf.append("Gregorian Date: " + gregorianDate + "\n");
            buf.append("Is Leap Year: " + isGregorianLeap + "\n");
            buf.append("Day of Year: " + dayOfYear + "\n");
            buf.append("Day of Week: " + dayOfWeek + "\n");
            buf.append("Chinese Year: " + chineseYear + "\n");
            buf.append("Heavenly Stem: " + ((chineseYear - 1) % 10) + "\n");
            buf.append("Earthly Branch: " + ((chineseYear - 1) % 12) + "\n");
            buf.append("Chinese Month: " + chineseMonth + "\n");
            buf.append("Chinese Date: " + chineseDate + "\n");
            buf.append("Sectional Term: " + sectionalTerm + "\n");
            buf.append("Principle Term: " + principleTerm + "\n");
            return buf.toString();
        }

        public String[] getYearTable() {
            setGregorian(gregorianYear, 1, 1);
            computeChineseFields();
            computeSolarTerms();
            String[] table = new String[58]; // 6*9 + 4
            table[0] = getTextLine(27, "公历年历：" + gregorianYear);
            table[1] = getTextLine(27, "农历年历：" + (chineseYear + 1) + " ("
                    + stemNames[(chineseYear + 1 - 1) % 10]
                    + branchNames[(chineseYear + 1 - 1) % 12] + " - "
                    + animalNames[(chineseYear + 1 - 1) % 12] + "年)");
            int ln = 2;
            String blank = "                                         " + "  "
                    + "                                         ";
            String[] mLeft = null;
            String[] mRight = null;
            for (int i = 1; i <= 6; i++) {
                table[ln] = blank;
                ln++;
                mLeft = getMonthTable();
                mRight = getMonthTable();
                for (int j = 0; j < mLeft.length; j++) {
                    String line = mLeft[j] + "  " + mRight[j];
                    table[ln] = line;
                    ln++;
                }
            }
            table[ln] = blank;
            ln++;
            table[ln] = getTextLine(0, "##/## - 公历日期/农历日期，(*)#月 - (闰)农历月第一天");
            ln++;
            return table;
        }


        public String getTextLine(int s, String t) {
            String str = "                                         " + "  "
                    + "                                         ";
            if (t != null && s < str.length() && s + t.length() < str.length())
                str = str.substring(0, s) + t + str.substring(s + t.length());
            return str;
        }

        private String[] monthNames = { "一", "二", "三", "四", "五", "六", "七",
                "八", "九", "十", "十一", "十二" };

        public String[] getMonthTable() {
            setGregorian(gregorianYear, gregorianMonth, 1);
            computeChineseFields();
            computeSolarTerms();
            String[] table = new String[8];
            String title = null;
            if (gregorianMonth < 11)
                title = "                   ";
            else
                title = "                 ";
            title = title + monthNames[gregorianMonth - 1] + "月"
                    + "                   ";
            String header = "   日    一    二    三    四    五    六 ";
            String blank = "                                          ";
            table[0] = title;
            table[1] = header;
            int wk = 2;
            String line = "";
            for (int i = 1; i < dayOfWeek; i++) {
                line += "     " + ' ';
            }
            int days = daysInGregorianMonth(gregorianYear, gregorianMonth);
            for (int i = gregorianDate; i <= days; i++) {
                line += getDateString() + ' ';
                rollUpOneDay();
                if (dayOfWeek == 1) {
                    table[wk] = line;
                    line = "";
                    wk++;
                }
            }
            for (int i = dayOfWeek; i <= 7; i++) {
                line += "     " + ' ';
            }
            table[wk] = line;
            for (int i = wk + 1; i < table.length; i++) {
                table[i] = blank;
            }
            for (int i = 0; i < table.length; i++) {
                table[i] = table[i].substring(0, table[i].length() - 1);
            }


            return table;
        }


        private String[] chineseMonthNames = { "正", "二", "三", "四", "五", "六",
                "七", "八", "九", "十", "冬", "腊" };
        private String[] principleTermNames = { "雨水", "春分", "谷雨", "夏满",
                "夏至", "大暑", "处暑", "秋分", "霜降", "小雪", "冬至", "大寒" };
        private String[] sectionalTermNames = { "立春", "惊蛰", "清明", "立夏",
                "芒种", "小暑", "立秋", "白露", "寒露", "立冬", "大雪", "小寒" };

        public String getDateString() {
            String str = "";
            String gm = String.valueOf(gregorianMonth);
            if (gm.length() == 1)
                gm = ' ' + gm;
            String cm = String.valueOf(Math.abs(chineseMonth));
            if (cm.length() == 1)
                cm = ' ' + cm;
            String gd = String.valueOf(gregorianDate);
            if (gd.length() == 1)
                gd = ' ' + gd;
            String cd = String.valueOf(chineseDate);
            if (cd.length() == 1)
                cd = ' ' + cd;
            if (gregorianDate == sectionalTerm) {
                str = " " + sectionalTermNames[gregorianMonth - 1];
            } else if (gregorianDate == principleTerm) {
                str = " " + principleTermNames[gregorianMonth - 1];
            } /*else if (chineseDate == 1 && chineseMonth > 0) {
                str = " " + chineseMonthNames[chineseMonth] + "月";
            } else if (chineseDate == 1 && chineseMonth < 0) {
                str = "*" + chineseMonthNames[-chineseMonth - 1] + "月";
            } else {
                str = gd + '/' + cd;
            }*/
            return str;
        }

        public int rollUpOneDay() {
            dayOfWeek = dayOfWeek % 7 + 1;
            dayOfYear++;
            gregorianDate++;
            int days = daysInGregorianMonth(gregorianYear, gregorianMonth);
            if (gregorianDate > days) {
                gregorianDate = 1;
                gregorianMonth++;
                if (gregorianMonth > 12) {
                    gregorianMonth = 1;
                    gregorianYear++;
                    dayOfYear = 1;
                    isGregorianLeap = isGregorianLeapYear(gregorianYear);
                }
                sectionalTerm = sectionalTerm(gregorianYear, gregorianMonth);
                principleTerm = principleTerm(gregorianYear, gregorianMonth);
            }
            chineseDate++;
            days = daysInChineseMonth(chineseYear, chineseMonth);
            if (chineseDate > days) {
                chineseDate = 1;
                chineseMonth = nextChineseMonth(chineseYear, chineseMonth);
                if (chineseMonth == 1)
                    chineseYear++;
            }
            return 0;
        }

        public int getGregorianYear() {
            return gregorianYear;
        }

        public void setGregorianYear(int gregorianYear) {
            this.gregorianYear = gregorianYear;
        }

        public int getGregorianMonth() {
            return gregorianMonth;
        }

        public void setGregorianMonth(int gregorianMonth) {
            this.gregorianMonth = gregorianMonth;
        }

        public int getGregorianDate() {
            return gregorianDate;
        }

        public void setGregorianDate(int gregorianDate) {
            this.gregorianDate = gregorianDate;
        }

        public boolean isGregorianLeap() {
            return isGregorianLeap;
        }

        public void setGregorianLeap(boolean isGregorianLeap) {
            this.isGregorianLeap = isGregorianLeap;
        }

        public int getDayOfYear() {
            return dayOfYear;
        }

        public void setDayOfYear(int dayOfYear) {
            this.dayOfYear = dayOfYear;
        }

        public int getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public int getChineseYear() {
            return chineseYear;
        }

        public void setChineseYear(int chineseYear) {
            this.chineseYear = chineseYear;
        }

        public int getChineseMonth() {
            return chineseMonth;
        }

        public void setChineseMonth(int chineseMonth) {
            this.chineseMonth = chineseMonth;
        }

        public int getChineseDate() {
            return chineseDate;
        }

        public void setChineseDate(int chineseDate) {
            this.chineseDate = chineseDate;
        }

        public int getSectionalTerm() {
            return sectionalTerm;
        }

        public void setSectionalTerm(int sectionalTerm) {
            this.sectionalTerm = sectionalTerm;
        }

        public int getPrincipleTerm() {
            return principleTerm;
        }

        public void setPrincipleTerm(int principleTerm) {
            this.principleTerm = principleTerm;
        }

        public char[] getDaysInGregorianMonth() {
            return daysInGregorianMonth;
        }

        public void setDaysInGregorianMonth(char[] daysInGregorianMonth) {
            this.daysInGregorianMonth = daysInGregorianMonth;
        }

        public String[] getStemNames() {
            return stemNames;
        }

        public void setStemNames(String[] stemNames) {
            this.stemNames = stemNames;
        }

        public String[] getBranchNames() {
            return branchNames;
        }

        public void setBranchNames(String[] branchNames) {
            this.branchNames = branchNames;
        }

        public String[] getAnimalNames() {
            return animalNames;
        }

        public void setAnimalNames(String[] animalNames) {
            this.animalNames = animalNames;
        }

        public char[] getChineseMonths() {
            return chineseMonths;
        }

        public void setChineseMonths(char[] chineseMonths) {
            this.chineseMonths = chineseMonths;
        }

        public int getBaseYear() {
            return baseYear;
        }


        public void setBaseYear(int baseYear) {
            this.baseYear = baseYear;
        }

        public int getBaseMonth() {
            return baseMonth;
        }

        public void setBaseMonth(int baseMonth) {
            this.baseMonth = baseMonth;
        }

        public int getBaseDate() {
            return baseDate;
        }

        public void setBaseDate(int baseDate) {
            this.baseDate = baseDate;
        }

        public int getBaseIndex() {
            return baseIndex;
        }

        public void setBaseIndex(int baseIndex) {
            this.baseIndex = baseIndex;
        }

        public int getBaseChineseYear() {
            return baseChineseYear;
        }

        public void setBaseChineseYear(int baseChineseYear) {
            this.baseChineseYear = baseChineseYear;
        }

        public int getBaseChineseMonth() {
            return baseChineseMonth;
        }

        public void setBaseChineseMonth(int baseChineseMonth) {
            this.baseChineseMonth = baseChineseMonth;
        }

        public int getBaseChineseDate() {
            return baseChineseDate;
        }

        public void setBaseChineseDate(int baseChineseDate) {
            this.baseChineseDate = baseChineseDate;
        }

        public int[] getBigLeapMonthYears() {
            return bigLeapMonthYears;
        }

        public void setBigLeapMonthYears(int[] bigLeapMonthYears) {
            this.bigLeapMonthYears = bigLeapMonthYears;
        }

        public char[][] getSectionalTermMap() {
            return sectionalTermMap;
        }

        public void setSectionalTermMap(char[][] sectionalTermMap) {
            this.sectionalTermMap = sectionalTermMap;
        }

        public char[][] getSectionalTermYear() {
            return sectionalTermYear;
        }

        public void setSectionalTermYear(char[][] sectionalTermYear) {
            this.sectionalTermYear = sectionalTermYear;
        }

        public char[][] getPrincipleTermMap() {
            return principleTermMap;
        }

        public void setPrincipleTermMap(char[][] principleTermMap) {
            this.principleTermMap = principleTermMap;
        }

        public char[][] getPrincipleTermYear() {
            return principleTermYear;
        }

        public void setPrincipleTermYear(char[][] principleTermYear) {
            this.principleTermYear = principleTermYear;
        }

        public String[] getMonthNames() {
            return monthNames;
        }

        public void setMonthNames(String[] monthNames) {
            this.monthNames = monthNames;
        }

        public String[] getChineseMonthNames() {
            return chineseMonthNames;
        }

        public void setChineseMonthNames(String[] chineseMonthNames) {
            this.chineseMonthNames = chineseMonthNames;
        }

        public String[] getPrincipleTermNames() {
            return principleTermNames;
        }

        public void setPrincipleTermNames(String[] principleTermNames) {
            this.principleTermNames = principleTermNames;
        }

        public String[] getSectionalTermNames() {
            return sectionalTermNames;
        }

        public void setSectionalTermNames(String[] sectionalTermNames) {
            this.sectionalTermNames = sectionalTermNames;
        }

        public void main(String[] arg) {
            CalendarUtil c = new CalendarUtil();
            String cmd = "day";
            int y = 2010;
            int m = 8;
            int d = 11;

            c.setGregorian(y, m, d);
            c.computeChineseFields();
            c.computeSolarTerms();

            if (cmd.equalsIgnoreCase("year")) {
                String[] t = c.getYearTable();
                for (int i = 0; i < t.length; i++)
                    System.out.println(t[i]);
            } else if (cmd.equalsIgnoreCase("month")) {
                String[] t = c.getMonthTable();
                for (int i = 0; i < t.length; i++)
                    System.out.println(t[i]);
            } else {
                System.out.println(c.toString());
            }
            System.out.println(c.getDateString());

        }
    }
}
