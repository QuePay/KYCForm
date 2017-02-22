package np.com.qpay.kycformapp.kyc.amountpicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import np.com.qpay.kycformapp.R;
import np.com.qpay.kycformapp.kyc.dto.AddressInfo;

/**
 * PopWindow for Date Pick
 */
public class ZonePickerPopWin extends PopupWindow implements OnClickListener {

    public Button cancelBtn;
    public Button confirmBtn;
    public ZoneLoopView monthLoopView;
    public View pickerContainerV;
    public View contentView;//root view

    private int monthPos = -1;

    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private boolean showDayMonthYear;
    private List<AddressInfo> values;
    private AddressInfo initialValue;

    List<String> monthList = new ArrayList();

    public static class Builder{

        //Required
        private Context context;
        private OnZonePickedListener listener;
        public Builder(Context context, OnZonePickedListener listener){
            this.context = context;
            this.listener = listener;
        }

        //Option
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;
        private int viewTextSize = 25;
        private int initialPosition = -1;
        private int interval = 1;
        private List<AddressInfo> values;
        private AddressInfo initialValue;

        public Builder textCancel(String textCancel){
            this.textCancel = textCancel;
            return this;
        }

        public Builder initialValue(AddressInfo initialValue){
            this.initialValue = initialValue;
            return this;
        }

        public Builder initialPostion(int initialPosition){
            this.initialPosition = initialPosition;
            return this;
        }

        public Builder textConfirm(String textConfirm){
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder colorCancel(int colorCancel){
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm){
            this.colorConfirm = colorConfirm;
            return this;
        }

        public Builder setInterval(int interval){
            this.interval = interval;
            return this;
        }

        public Builder setValues(List<AddressInfo> values){
            this.values = values;
            return this;
        }

        /**
         * set btn text btnTextSize
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize){
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize){
            this.viewTextSize = textSize;
            return this;
        }

        public ZonePickerPopWin build(){
            return new ZonePickerPopWin(this);
        }
    }

    public ZonePickerPopWin(Builder builder){
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.values = builder.values;
        this.monthPos = builder.initialPosition;
        this.initialValue = builder.initialValue;
        initView();
    }

    private OnZonePickedListener mListener;

    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(showDayMonthYear ? R.layout.layout_zone_picker_inverted : R.layout.layout_zone_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        monthLoopView = (ZoneLoopView) contentView.findViewById(R.id.picker_month);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        monthLoopView.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                monthPos = item;
            }
        });
        if(values == null){
            initPickerViews(); // init year and month loop view
        }else {
            initPickerViewsWithValues();
        }

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init year and month loop view,
     * Let the day loop view be handled separately
     */
    private void initPickerViews() {

        monthPos = (monthList.size() / 2) - 1;
        monthLoopView.setDataList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);

    }

    private void initPickerViewsWithValues(){
        for(AddressInfo value: values){
            monthList.add(value.getName());
        }

        if(monthPos < 0 && monthPos > values.size()) {
            monthPos = (monthList.size() / 2) - 1;
        }else {
            if(initialValue != null){
                monthPos = monthList.indexOf(initialValue.getName());
            }else {
                monthPos = 0;
            }
        }

        monthLoopView.setDataList((ArrayList) monthList);
        monthLoopView.setInitPosition(monthPos);
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {

        if (v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {
                String month = monthList.get(monthPos);
                mListener.onZonePickCompleted(monthPos, month);
            }

            dismissPopWin();
        }
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnZonePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param month
         * @param dateDesc  yyyy-MM-dd
         */
        void onZonePickCompleted(int month, String dateDesc);
    }
}