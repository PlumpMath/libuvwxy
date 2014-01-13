package de.uvwxy.osmdroid;

import java.util.List;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.safecanvas.SafeTranslatedCanvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * An itemized overlay with an InfoWindow or "bubble" which opens when the user
 * taps on an overlay item, and displays item attributes. <br>
 * Items must be ExtendedOverlayItem. <br>
 * 
 * 
 * @see CardOverlayItem
 * @see InfoWindow
 * 
 * @author Original author M.Kergall, modified by Paul Smith
 */
public class ItemizedOverlayWithCard<Item extends ExtendedOverlayItem> extends ItemizedIconOverlay<Item> {
	;
	protected InfoWindow mBubble; //only one for all items of this overlay => one at a time
	protected Item cardItem; //the item currently showing the card. Null if none. 

	private static final SafeTranslatedCanvas sSafeCanvas = new SafeTranslatedCanvas();

	static int layoutResId = 0;

	public ItemizedOverlayWithCard(final Context context, final List<Item> aList, final MapView mapView,
			final InfoWindow bubble) {
		super(context, aList, new OnItemGestureListener<Item>() {
			@Override
			public boolean onItemSingleTapUp(final int index, final Item item) {
				return false;
			}

			@Override
			public boolean onItemLongPress(final int index, final Item item) {
				return false;
			}
		});
		if (bubble != null) {
			mBubble = bubble;
		} else {
			//build default bubble:
			String packageName = context.getPackageName();

			if (layoutResId == 0) {
				layoutResId = context.getResources().getIdentifier("layout/bonuspack_bubble", null, packageName);
				if (layoutResId == 0)
					Log.e(BonusPackHelper.LOG_TAG, "ItemizedOverlayWithBubble: layout/bonuspack_bubble not found in "
							+ packageName);
			}
			mBubble = new DefaultInfoWindow(layoutResId, mapView);
		}
		cardItem = null;
	}

	public ItemizedOverlayWithCard(final Context context, final List<Item> aList, final MapView mapView) {
		this(context, aList, mapView, null);
	}

	/**
	 * Opens the bubble on the item. For each ItemizedOverlay, only one bubble
	 * is opened at a time. If you want more bubbles opened simultaneously, use
	 * many ItemizedOverlays.
	 * 
	 * @param index
	 *            of the overlay item to show
	 * @param mapView
	 */
	public void showBubbleOnItem(final int index, final MapView mapView, boolean panIntoView) {
		Item eItem = getItem(index);
		cardItem = eItem;
		if (eItem != null) {
			eItem.showBubble(mBubble, mapView, panIntoView);
			//setFocus((Item)eItem);
		}
	}

	/**
	 * Close the bubble (if it's opened).
	 */
	public void hideBubble() {
		mBubble.close();
		cardItem = null;
	}

	@Override
	protected boolean onSingleTapUpHelper(final int index, final Item item, final MapView mapView) {
		Log.d("OVERLAY", "onTap item");
		showBubbleOnItem(index, mapView, true);
		mapView.refreshDrawableState();
		return true;
	}

	/** @return the item currently showing the bubble, or null if none. */
	public OverlayItem getBubbledItem() {
		if (mBubble.isOpen())
			return cardItem;
		else
			return null;
	}

	/**
	 * @return the index of the item currently showing the bubble, or -1 if
	 *         none.
	 */
	public int getBubbledItemId() {
		OverlayItem item = getBubbledItem();
		if (item == null)
			return -1;
		else
			return mItemList.indexOf(item);
	}

	@Override
	public synchronized Item removeItem(final int position) {
		Item result = super.removeItem(position);
		if (cardItem == result) {
			hideBubble();
		}
		return result;
	}

	@Override
	public synchronized boolean removeItem(final Item item) {
		boolean result = super.removeItem(item);
		if (cardItem == item) {
			hideBubble();
		}
		return result;
	}

	@Override
	public synchronized void removeAllItems() {
		super.removeAllItems();
		hideBubble();
	}

	@Override
	public synchronized void draw(final Canvas canvas, final MapView mapView, final boolean shadow) {
		final Projection pj = mapView.getProjection();
		final int size = mItemList.size() - 1;
		final Point mCurScreenCoords = new Point();

		sSafeCanvas.setCanvas(canvas);

		// Find the screen offset
		Rect screenRect = mapView.getProjection().getScreenRect();
		sSafeCanvas.xOffset = -screenRect.left;
		sSafeCanvas.yOffset = -screenRect.top;

		Paint p = new Paint();
		p.setColor(Color.GRAY);
		p.setAntiAlias(true);

		if (shadow) {
			return;
		}

		for (int i = size; i >= 0; i--) {
			final Item item = getItem(i);
			if (item instanceof CardOverlayItem<?>) {
				CardOverlayItem<?> e = (CardOverlayItem<?>) item;
				Object o = e.getRelatedObject();
				if (o == null) {
					continue;
				}
				pj.toMapPixels(item.getPoint(), mCurScreenCoords);
				e.getExtractor().draw(sSafeCanvas.getWrappedCanvas(), mCurScreenCoords, pj);
			}
		}
		
		/*
		 * Draw in backward cycle, so the items with the least index are on the
		 * front.
		 */
		for (int i = size; i >= 0; i--) {
			final Item item = getItem(i);
			if (item != cardItem) {
				pj.toMapPixels(item.getPoint(), mCurScreenCoords);
				Log.d("UVWXY", "x " + mCurScreenCoords.x);
				Log.d("UVWXY", "y " + mCurScreenCoords.y);
				onDrawItem(sSafeCanvas, item, mCurScreenCoords, 0f);
			}
		}
		//draw focused item last:
		if (cardItem != null) {
			pj.toMapPixels(cardItem.getPoint(), mCurScreenCoords);
			onDrawItem(sSafeCanvas, (Item) cardItem, mCurScreenCoords, 0f);
		}
	}

	public void repopulate() {
		populate();
	}

}
