package de.uvwxy.osmdroid;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.MapView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Preconditions;

public class CardOverlay<E> {
	private Class<? extends CardOverlayObjectConverter<E>> converterClass;
	private ItemizedOverlayWithCard<CardOverlayItem<E>> overlay;

	public CardOverlay(Class<? extends CardOverlayObjectConverter<E>> converterClass, Context ctx, MapView mv, Activity act) {
		Preconditions.checkNotNull(converterClass);
		Preconditions.checkNotNull(ctx);
		Preconditions.checkNotNull(mv);

		this.converterClass = converterClass;

		overlay = new ItemizedOverlayWithCard<CardOverlayItem<E>>(ctx, new ArrayList<CardOverlayItem<E>>(),
				mv, new CardItem(mv, act, ctx));

	}

	public ItemizedOverlayWithCard<CardOverlayItem<E>> getOverlay() {
		return overlay;
	}

	public void replaceObjects(Context ctx, List<E> list) {
		Preconditions.checkNotNull(ctx);

		overlay.removeAllItems();
		for (E e : list) {
			addObject(ctx, e);
		}
	}

	private void addObject(Context ctx, E e) {
		Preconditions.checkNotNull(ctx);

		if (e == null) {
			return;
		}

		CardOverlayObjectConverter<E> converter = null;

		Constructor<? extends CardOverlayObjectConverter<E>> constr;
		try {
			constr = converterClass.getConstructor(e.getClass());
			converter = constr.newInstance(e);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		}

		if (converter == null) {
			Log.d("UVWXY", "add object failed " + e.toString());
			return;
		}

		CardOverlayItem<E> item = new CardOverlayItem<E>(converter, ctx);
		overlay.addItem(item);
	}

}
