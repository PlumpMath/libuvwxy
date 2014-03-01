package de.uvwxy.panzoom;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PanZoomListener implements OnTouchListener {
	PointF start = new PointF();
	float oldZoomDistPixels = 1f;
	boolean actionClick = false;
	// use this flag to prevent the initial scale value destroy everything
	// usualy scale is somewhere around 1.0f, but initial around half screen size.
	boolean reset_first_scale_value = true;

	private PanZoomType state = PanZoomType.NONE;
	private boolean onTouchReturn = true;
	private PanZoomResult panZoomResult = new PanZoomResult();

	public PanZoomResult getPanZoomResult() {
		return panZoomResult;
	}

	public void setOnTouchReturn(boolean onTouchReturn) {
		this.onTouchReturn = onTouchReturn;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			state = PanZoomType.PAN;
			start.set(event.getX(), event.getY());
			actionClick = true;
			panZoomResult.resetResult();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// second finger down
			state = PanZoomType.ZOOM;
			oldZoomDistPixels = 1f;
			reset_first_scale_value = true;
			actionClick = false;
			panZoomResult.resetResult();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			state = PanZoomType.NONE;
			float t = panZoomResult.x + panZoomResult.y;
			if (t == 0) {
				panZoomResult.type = PanZoomType.CLICK;
			} else {
				panZoomResult.resetResult();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			switch (state) {
			case PAN:
				PointF pan = new PointF(event.getX() - start.x, event.getY() - start.y);
				start.set(event.getX(), event.getY());

				panZoomResult.type = PanZoomType.PAN;
				panZoomResult.x = pan.x;
				panZoomResult.y = pan.y;
				break;
			case ZOOM:
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				float d = (float) Math.sqrt(x * x + y * y);
				float s = d / oldZoomDistPixels;
				if (reset_first_scale_value) {
					s = 1f;
					reset_first_scale_value = false;
				}
				//				Log.i("FOOTPATH", "PanZoomListener: scaled: " + (s));
				oldZoomDistPixels = d;

				panZoomResult.type = PanZoomType.ZOOM;
				panZoomResult.scale = s;
				break;
			case CLICK:
				break;
			case NONE:
				break;
			default:
				break;
			}
			break;
		}
		return onTouchReturn; // indicate false, as this is to be done by calling function
	}

}
