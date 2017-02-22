package np.com.qpay.kycformapp.custumclasses;

/**
 * Created by deadlydragger on 7/14/16.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextViewbold extends TextView {


    public FontTextViewbold(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
        this.setTypeface(face);
    }

    public FontTextViewbold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
        this.setTypeface(face);
    }

    public FontTextViewbold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}