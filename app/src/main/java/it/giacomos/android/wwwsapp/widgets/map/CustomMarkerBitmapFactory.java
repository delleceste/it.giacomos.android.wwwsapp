package it.giacomos.android.wwwsapp.widgets.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class CustomMarkerBitmapFactory 
{
	private int mCachedFontSize = -1;
	private float mInitialFontSize = 25.0f;
	private int  mTextBgColor;
	private float mTextWidthScaleFactor;
	private int mAlphaTextContainer;
	
	public void setTextWidthScaleFactor(float factor)
	{
		mTextWidthScaleFactor = factor;
	}
	
	public void setAlphaTextContainer(int alpha)
	{
		mAlphaTextContainer = alpha;
		mTextBgColor = Color.argb(mAlphaTextContainer, 250, 252, 255);
	}
	
	public void setInitialFontSize(float fontSize)
	{
		mInitialFontSize = fontSize;
	}
	
	public float getCachedFontSize()
	{
		return mCachedFontSize;
	}
	
	public CustomMarkerBitmapFactory(Resources res)
	{
		DisplayMetrics dm = res.getDisplayMetrics();
		if(dm.densityDpi == DisplayMetrics.DENSITY_LOW)
			mInitialFontSize = 12;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_MEDIUM)
			mInitialFontSize = 16;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_HIGH)
			mInitialFontSize = 26;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_XHIGH)
			mInitialFontSize = 28;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_XXHIGH)
			mInitialFontSize = 32;
		else if(dm.densityDpi == DisplayMetrics.DENSITY_XXXHIGH)
			mInitialFontSize = 36;
		
		mAlphaTextContainer = 230;
		mTextBgColor = Color.argb(mAlphaTextContainer, 250, 252, 255);	
		mTextWidthScaleFactor = 2.4f;
	}
	
	/*  <iconW>
	 * +------+--+
	 * |      |  | <- iconH
	 * | icon |  |
	 * +---------+           ---
	 * |  text   | <- textH   | textH + marginH
	 * +---------+           ---
	 * <--- + --->
	 *      \
	 *       max(textContainerW, iconW)
	 */
	public BitmapDescriptor getIcon(Bitmap icon0, String label)
	{
		int iconW, iconH, textContainerW, textContainerH, marginW = 8, marginH = 8;
		int textW, textH;
		iconW = icon0.getWidth();
		iconH = icon0.getHeight();
		textW = (int) Math.round(iconW * mTextWidthScaleFactor);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		if(mCachedFontSize < 0) /* start from a certain font size and then scale it afterwards */
			paint.setTextSize(mInitialFontSize);
		else
			paint.setTextSize(mCachedFontSize);
		
		Rect bounds = new Rect();
		paint.getTextBounds(label, 0, label.length(), bounds);
		/* scale text while its length or height fall outside bounds */
		while(bounds.width() > textW)
		{
			paint.setTextSize(paint.getTextSize() - 1);
			paint.getTextBounds(label, 0, label.length(), bounds);
		}
		textH = bounds.height();
		textW = bounds.width();
		textContainerW = textW + marginW;
		textContainerH = textH + marginH;
		Bitmap bitmap = Bitmap.createBitmap(Math.max(textContainerW, iconW), iconH + textContainerH, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		/* draw the text */
		paint.setColor(mTextBgColor);
		canvas.drawRoundRect(new RectF(bitmap.getWidth() - textContainerW, iconH, textContainerW, textContainerH + iconH), marginW/2, marginH/2, paint);
		paint.setColor(Color.BLACK);
		/* draw the original bitmap */
		canvas.drawBitmap(icon0, 0, 0, paint);
		canvas.drawText(label, bitmap.getWidth() - textContainerW + marginW / 2,
				iconH + textContainerH - marginH / 2, paint);
		
		if(mCachedFontSize != Math.round(paint.getTextSize()))
			mCachedFontSize = Math.round(paint.getTextSize()); /* save it for future use (optimization) */
		
		return BitmapDescriptorFactory.fromBitmap(bitmap);	
	}
}
