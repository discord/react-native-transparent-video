package com.transparentvideo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransparentVideoViewManager extends SimpleViewManager<LinearLayout> {

  private static List<LinearLayout> sInstances = new ArrayList<>();

  public static final String REACT_CLASS = "TransparentVideoView";
  private static final String TAG = "TransparentVideoViewManager";

  private boolean autoplay = false;
  private boolean loop = false;

  ReactApplicationContext reactContext;

  public TransparentVideoViewManager(ReactApplicationContext reactContext) {
    this.reactContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public LinearLayout createViewInstance(ThemedReactContext reactContext) {
    LinearLayout view = new LinearLayout(this.reactContext);
    sInstances.add(view);
    return view;
  }

  public static void destroyView(LinearLayout view) {
    sInstances.remove(view);
  }

  @ReactProp(name = "autoplay")
  public void setAutoplay(LinearLayout view, boolean autoplay) {
    this.autoplay = autoplay;
  }

  @ReactProp(name = "loop")
  public void setLoop(LinearLayout view, boolean loop) {
    this.loop = loop;
  }

  @ReactProp(name = "src")
  public void setSrc(LinearLayout view, ReadableMap src) {
    AlphaMovieView alphaMovieView = (AlphaMovieView)view.getChildAt(0);
    if (alphaMovieView == null) {
      alphaMovieView = new AlphaMovieView(reactContext, null);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
      lp.gravity = Gravity.CENTER;
      alphaMovieView.setLayoutParams(lp);
      alphaMovieView.setAutoPlayAfterResume(true);
      alphaMovieView.setAutoPlayAfterInit(this.autoplay);
      alphaMovieView.setLooping(this.loop);
      view.addView(alphaMovieView);
    }
    alphaMovieView.setPacked(true);
    String file = src.getString("uri").toLowerCase();
    Log.d(TAG + " setSrc", "file: " + file);
    try {
      Integer rawResourceId = Utils.getRawResourceId(reactContext, file);
      Log.d(TAG + " setSrc", "ResourceID: " + rawResourceId);

      alphaMovieView.setVideoFromResourceId(reactContext, rawResourceId);
    } catch (RuntimeException e) {
      Log.e(TAG + " setSrc", e.getMessage(), e);
      alphaMovieView.setVideoByUrl(file);
    }
  }
}
