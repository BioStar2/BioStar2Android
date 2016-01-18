package com.tekinarslan.material.sample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supremainc.biostar2.R;


public class FloatingActionButton extends Button {

    Drawable drawableIcon;
    ImageView icon; // Icon of float button
    int sizeIcon = 24;
    int sizeRadius = 28;
    private boolean mIsUp;

    public FloatingActionButton(Context context, AttributeSet attributes) {
        super(context, attributes);
        setBackgroundResource(R.drawable.layer_btn_fab);
        sizeRadius = 28;
        setDefaultProperties();
        icon = new ImageView(context);
        setDirectioDown();
//        if (drawableIcon != null) {
//            try {
//                icon.setBackground(drawableIcon);
//            } catch (NoSuchMethodError e) {
//                icon.setBackgroundDrawable(drawableIcon);
//            }
//        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dpToPx(sizeIcon, getResources()), dpToPx(sizeIcon, getResources()));
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
//      params.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
//      params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        icon.setLayoutParams(params);
        addView(icon);
    }

    public Bitmap cropCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

//        final int color = 0xff424242;
        final int color = 0xff60b2a6;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }

    public int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    @Override
    public TextView getTextView() {
        return null;
    }

    protected void setDefaultProperties() {
        rippleSpeed = dpToPx(2, getResources());
        rippleSize = dpToPx(5, getResources());
        super.minWidth = sizeRadius * 2;
        super.minHeight = sizeRadius * 2;
        super.background = R.drawable.layer_btn_fab;
        super.setDefaultProperties();
    }

    public boolean getDirectionUp() {
        return mIsUp;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public void setDrawableIcon(Drawable drawableIcon) {
        this.drawableIcon = drawableIcon;
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                icon.setBackground(drawableIcon);
            } else {
                icon.setBackgroundDrawable(drawableIcon);
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {
            Rect src = new Rect(0, 0, getWidth(), getHeight());
            Rect dst = new Rect(dpToPx(1, getResources()), dpToPx(2, getResources()), getWidth() - dpToPx(1, getResources()), getHeight() - dpToPx(2, getResources()));
            Bitmap bmp = cropCircle(makeCircle());
            canvas.drawBitmap(bmp, src, dst, null);
            bmp.recycle();
            invalidate();
        }

    }

    public void setDirectioDown() {
        mIsUp = false;
        icon.setImageResource(R.drawable.ic_list_down);
        icon.invalidate();
    }

    public void setDirectioUp() {
        mIsUp = true;
        icon.setImageResource(R.drawable.ic_list_top);
        icon.invalidate();
    }
}
