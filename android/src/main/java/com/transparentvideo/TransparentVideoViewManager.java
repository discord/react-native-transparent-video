package com.transparentvideo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.bridge.ReadableMap;

import java.util.ArrayList;
import java.util.List;

public class TransparentVideoViewManager extends SimpleViewManager<LinearLayout> {

  private static List<LinearLayout> sInstances = new ArrayList<>();

  public static final String REACT_CLASS = "TransparentVideoView";
  private static final String TAG = "TVVManager";

  ReactApplicationContext reactContext;

  public enum PlaybackCommand {
    PLAY,
    PAUSE,
    STOP,
    RESET,
  }

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

  @Override
  public void onDropViewInstance(@NonNull LinearLayout view) {
    super.onDropViewInstance(view);

    AlphaMovieView alphaMovieView = getAlphaMovieView(view);
    if (alphaMovieView != null) {
      alphaMovieView.cleanup();
    }

    sInstances.remove(view);
  }


  @ReactProp(name = "src")
  public void setSrc(LinearLayout view, ReadableMap src) {
    if (src == null) {
      return;
    }

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    String uri = src.getString("uri").toLowerCase();

    Log.d(TAG, "setSrc - src.uri: " + uri);

    try {
      int rawResourceId = Utils.getRawResourceId(reactContext, uri);

      if (rawResourceId != 0) {
        Log.d(TAG, "setSrc - Setting to resource ID: " + rawResourceId);

        alphaMovieView.setVideoFromResourceId(reactContext, rawResourceId);
      } else {
        Log.d(TAG, "setSrc - Setting to URI: " + uri);

        alphaMovieView.setVideoByUrl(uri);
      }
    } catch (Resources.NotFoundException e) {
      Log.e(TAG, "setSrc -  Error: " + e.getMessage(), e);

      alphaMovieView.setVideoByUrl(uri);
    }
  }

  @ReactProp(name = "autoplay")
  public void setAutoplay(LinearLayout view, boolean autoplay) {
    Log.d(TAG, "setAutoplay - autoplay: " + autoplay);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.setAutoPlayAfterResume(autoplay);

    if (autoplay) {
      alphaMovieView.start();
    } else {
      alphaMovieView.pause();
    }
  }

  @ReactProp(name = "loop")
  public void setLooping(LinearLayout view, boolean loop) {
    Log.d(TAG, "setLooping - loop: " + loop);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.setLooping(loop);
  }

  @ReactProp(name = "loopDelayMs")
  public void setLoopDelay(LinearLayout view, int loopDelayMs) {
    Log.d(TAG, "setLoopDelay - loopDelayMs: " + loopDelayMs);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.setLoopDelayMs(loopDelayMs);
  }

  @ReactProp(name = "loopStartMs")
  public void setLoopStart(LinearLayout view, int loopStartMs) {
    Log.d(TAG, "setLoopStart - loopStartMs: " + loopStartMs);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.setLoopStartMs(loopStartMs);
  }

  @ReactProp(name = "loopEndMs")
  public void setLoopEnd(LinearLayout view, int loopEndMs) {
    Log.d(TAG, "setLoopEnd - loopEndMs: " + loopEndMs);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.setLoopEndMs(loopEndMs);
  }

  @ReactProp(name = "seekToMs")
  public void setSeekTo(LinearLayout view, int seekToMs) {
    Log.d(TAG, "setSeekTo - seekToMs: " + seekToMs);

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);
    alphaMovieView.seekTo(seekToMs);
  }

  @ReactProp(name = "playbackCommand")
  public void executePlaybackCommand(LinearLayout view, String playbackCommandString) {
    PlaybackCommand playbackCommand;

    if (playbackCommandString == null || playbackCommandString.isEmpty()) {
      return;
    }

    Log.d(TAG, "executePlaybackCommand - playbackCommand: " + playbackCommandString);

    try {
      playbackCommand = PlaybackCommand.valueOf(playbackCommandString.toUpperCase());
    } catch (IllegalArgumentException e) {
      Log.e(TAG, "executePlaybackCommand - Unknown command: " + playbackCommandString);
      return;
    }

    AlphaMovieView alphaMovieView = getOrCreateAlphaMovieView(view);

    switch (playbackCommand) {
      case PLAY:
        alphaMovieView.start();
        break;
      case PAUSE:
        alphaMovieView.pause();
        break;
      case STOP:
        alphaMovieView.stop();
        break;
      case RESET:
        alphaMovieView.reset();
        break;
      default:
        Log.w(TAG, "executePlaybackCommand - Unhandled command: " + playbackCommand);
        break;
    }
  }

  private AlphaMovieView createAlphaMovieView(LinearLayout view) {
    AlphaMovieView newView = new AlphaMovieView(reactContext, null);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
    lp.gravity = Gravity.CENTER;
    newView.setLayoutParams(lp);
    newView.setAutoPlayAfterResume(true);
    newView.setPacked(true);

    view.addView(newView);

    return newView;
  }

  private AlphaMovieView getAlphaMovieView(LinearLayout view) {
    View child = view.getChildAt(0);

    if (child instanceof AlphaMovieView) {
      return (AlphaMovieView) child;
    }

    return null;
  }

  private AlphaMovieView getOrCreateAlphaMovieView(LinearLayout view) {
    AlphaMovieView alphaMovieView = getAlphaMovieView(view);

    return alphaMovieView != null ? alphaMovieView : createAlphaMovieView(view);
  }
}
