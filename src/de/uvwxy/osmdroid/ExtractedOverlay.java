package de.uvwxy.osmdroid;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.google.common.base.Preconditions;

public class ExtractedOverlay<E> {
	private AOverlayExtractor<E> extractor;
	private ItemizedOverlayWithBubble<ExtendedOverlayItem> overlay;

	public ExtractedOverlay(AOverlayExtractor<E> extractor, Context ctx, MapView mv, Activity act) {
		Preconditions.checkNotNull(extractor);
		Preconditions.checkNotNull(ctx);
		Preconditions.checkNotNull(mv);

		overlay = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(ctx, new ArrayList<ExtendedOverlayItem>(), mv,
				new NodeBubble(mv, act, ctx));
		this.extractor = extractor;
	}

	public ItemizedOverlayWithBubble<ExtendedOverlayItem> getOverlay() {
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

		GeoPoint aGeoPoint = new GeoPoint(extractor.getLocation(e));

		ExtendedOverlayItem item = //
		new ExtendedOverlayItem(extractor.getTitle(ctx, e), extractor.getDescription(ctx, e), aGeoPoint, ctx);
		item.setMarker(extractor.getMapIcon(ctx, e));

		if (extractor.getSubDescription(ctx, e) != null) {
			item.setSubDescription(extractor.getSubDescription(ctx, e));
		}
		
		item.setRelatedObject(e);
		overlay.addItem(item);
	}

}
