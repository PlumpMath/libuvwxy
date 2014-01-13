package de.uvwxy.osmdroid;

import org.osmdroid.views.MapView.Projection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;

public abstract class CardOverlayObjectConverter<E> {
	protected E e;

	public CardOverlayObjectConverter(E e) {
		this.e = e;
	}

	public E getE() {
		return e;
	}

	public abstract Location getLocation();

	public abstract String getTitle(Context ctx);

	public abstract String getDescription(Context ctx);

	public String getSubDescription(Context ctx) {
		return null;
	}

	public abstract Drawable getMapIcon(Context ctx);

	public abstract void draw(Canvas canvas, Point mCurScreenCoords, Projection pj);

}
