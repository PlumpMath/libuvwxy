package de.uvwxy.osmdroid;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Preconditions;

public class NodeBubble extends DefaultInfoWindow {
	private Object o;
	private Context ctx;
	private Activity act;

	public NodeBubble(MapView mapView, final Activity act, Context ctx) {
		super(R.layout.activity_list_item, mapView);

		Preconditions.checkNotNull(ctx);
		Preconditions.checkNotNull(act);
		this.ctx = ctx;
		this.act = act;

//		Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
		// bonuspack_bubble layouts already contain a "more info" button.
//		btn.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				if (o != null) {
////					if (mNodeLocData.hasNodeId()) {
////						IntentTools.showNodeData(act, mNodeLocData.getNodeId());
////					}
//				}
//			}
//		});
	}

	@Override
	public void onOpen(Object item) {
		super.onOpen(item);
		Log.i("NODEBUBBLE", "onOpen");
		Object o = ((ExtendedOverlayItem)item).getRelatedObject();
		if (o == null) {
			Log.i("NODEBUBBLE", "Object was null");
			return;
		}

//		if (o instanceof NodeLocationData) {
//			mNodeLocData = (NodeLocationData) ((ExtendedOverlayItem)item).getRelatedObject();
//			setupNodeLocBubble();
//		}

	}

	@Override
	public void onClose() {
		super.onClose();
	}

	

	ImageView imageView;

	private void setupNodeLocBubble() {
		// Fetch the thumbnail in background
		if (o != null) {
			//imageView = (ImageView) mView.findViewById(R.id.bubble_image);
			//int dp = 65;
			//int pix = BitmapTools.dipToPixels(ctx, dp);
			imageView.setVisibility(View.INVISIBLE);
		}

		// Show or hide "more info" button:
		if (o != null) {
//			mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
		} 
	}
}
