package de.uvwxy.math;

import android.graphics.Matrix;
import android.graphics.Point;

public class PointMath {

	public static Point rotate(Point myPoint, Point center, float degrees) {
		// taken from:
		// http://stackoverflow.com/questions/7795028/get-new-position-of-coordinate-after-rotation-with-matrix
		Matrix transform = new Matrix();
		// This is to rotate about the Rectangles center
		transform.setRotate(degrees, center.x, center.y);

		// Create new float[] to hold the rotated coordinates
		float[] pts = new float[2];

		// Initialize the array with our Coordinate
		pts[0] = myPoint.x;
		pts[1] = myPoint.y;

		// Use the Matrix to map the points
		transform.mapPoints(pts);

		// NOTE: pts will be changed by transform.mapPoints call
		// after the call, pts will hold the new cooridnates

		// Now, create a new Point from our new coordinates
		Point newPoint = new Point((int) pts[0], (int) pts[1]);

		// Return the new point
		return newPoint;
	}
}
