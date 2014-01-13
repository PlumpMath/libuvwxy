package de.uvwxy.osmdroid;

import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.util.GeoPoint;

import android.content.Context;

public class CardOverlayItem<E> extends ExtendedOverlayItem {
	CardOverlayObjectConverter<E> extractor;

	public CardOverlayItem(CardOverlayObjectConverter<E> extractor, Context ctx) {
		super(extractor.getTitle(ctx), extractor.getDescription(ctx), new GeoPoint(extractor.getLocation()), ctx);
		this.extractor = extractor;

		setMarker(extractor.getMapIcon(ctx));

		if (extractor.getSubDescription(ctx) != null) {
			setSubDescription(extractor.getSubDescription(ctx));
		}

		setRelatedObject(extractor.getE());
	}

	public CardOverlayObjectConverter<E> getExtractor() {
		return extractor;
	}
}
