package de.uvwxy.osmdroid;

import org.osmdroid.views.MapView.Projection;

import de.uvwxy.math.PointMath;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;

public abstract class AOverlayExtractor<E> {
	private E e;
	
	public E getE() {
		return e;
	}
	
	public void setE(E e) {
		this.e = e;
	}
	
	public abstract Location getLocation(E e);
	
	public abstract String getTitle(Context ctx, E e);

	public abstract String getDescription(Context ctx, E e);

	public String getSubDescription(Context ctx, E e) {
		return null;
	}

	public abstract Drawable getMapIcon(Context ctx, E e);

	public void  draw(Canvas canvas, Point mCurScreenCoords, Projection pj) {
		Location tempLoc = getLocation(getE());
		Paint p = new Paint();
		p.setColor(Color.GRAY);
		p.setAntiAlias(true);


		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y, 6, p);
		p.setStrokeWidth(1);
		p.setColor(Color.RED);
		canvas.drawLine(mCurScreenCoords.x, mCurScreenCoords.y - 3, mCurScreenCoords.x,
				mCurScreenCoords.y + 3, p);
		canvas.drawLine(mCurScreenCoords.x - 3, mCurScreenCoords.y, mCurScreenCoords.x + 3,
				mCurScreenCoords.y, p);
		
		double accuracy = tempLoc.getAccuracy();
		p.setColor(Color.YELLOW);
		p.setAlpha(50);
		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y,
				(float) accuracy * pj.metersToEquatorPixels(1), p);
		p.setColor(Color.BLACK);
		p.setAlpha(255);
		p.setStyle(Style.STROKE);
		canvas.drawCircle(mCurScreenCoords.x, mCurScreenCoords.y,
				(float) accuracy * pj.metersToEquatorPixels(1), p);
		p.setStyle(Style.FILL_AND_STROKE);
		p.setColor(Color.GREEN);
		p.setStrokeWidth(3);

		float rotation = tempLoc.getBearing();
		Point rot = new Point();
		rot.x = mCurScreenCoords.x + 32;
		rot.y = mCurScreenCoords.y;

		// fix to compass 0 degrees going up
		rotation -= 90;
		rot = PointMath.rotate(rot, mCurScreenCoords, rotation);

		canvas.drawLine(mCurScreenCoords.x, mCurScreenCoords.y, rot.x, rot.y, p);
	}

}
