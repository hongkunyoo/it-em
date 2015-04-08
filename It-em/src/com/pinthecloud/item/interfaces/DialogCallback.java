package com.pinthecloud.item.interfaces;

import android.os.Bundle;

public interface DialogCallback {
	public void doPositive(Bundle bundle);
	public void doNeutral(Bundle bundle);
	public void doNegative(Bundle bundle);
}
