package np.com.qpay.kycformapp.custumclasses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by deadlydragger on 7/17/16.
 */
public class FontTextViewLight  extends TextView {


    public FontTextViewLight(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
        this.setTypeface(face);
    }

    public FontTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
        this.setTypeface(face);
    }

    public FontTextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Light.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}