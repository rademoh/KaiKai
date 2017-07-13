package com.ng.techhouse.tinggqr.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.BaseAdapter;

import com.ng.techhouse.tinggqr.Login;
import com.ng.techhouse.tinggqr.MainActivity;

public abstract class Timeout extends BaseAdapter {

	private static final long timeout = 1300000 * 10; // 21 minute ... Timeout in milliseconds

	private static Handler handler;
	private static Runnable runnable;



	// Call this method only once, at the launch of the application for example
	public static void init(final  Context context) {


		handler = new Handler();
		runnable = new Runnable() {


			@Override
			public void run() {


				context.startActivity(new Intent(context, Login.class).putExtra("timeout", "timeout").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				android.os.Process.killProcess(android.os.Process.myPid());
				stop();

			}
		};

		handler.postDelayed(runnable, timeout);
	}

	// Call this method in "onTouchEvent" feature on each activity.
	// This will reset the counter to 0 at each user action
	public static void reset() {

		if(handler != null && runnable != null) {

			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable, timeout);
		}
	}
	public static void stop() {

		if(handler != null && runnable != null)
			handler.removeCallbacks(runnable);
	}
}
