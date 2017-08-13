package ru.facilicom24.manager.services;

import android.graphics.PointF;

public class MaxLocation {
	private PointF coordinates;
	private boolean isFromMockProvider;

	MaxLocation(PointF coordinates, boolean isFromMockProvider) {
		this.coordinates = coordinates;
		this.isFromMockProvider = isFromMockProvider;
	}

	public PointF getCoordinates() {
		return coordinates;
	}

	public boolean getIsFromMockProvider() {
		return isFromMockProvider;
	}
}
