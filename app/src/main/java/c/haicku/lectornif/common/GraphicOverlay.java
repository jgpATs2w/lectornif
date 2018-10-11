// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package c.haicku.lectornif.common;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 * <p>Supports scaling and mirroring of the graphics relative the camera's preview properties. The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 *
 * <p>Associated {@link Graphic} items should use the following methods to convert to view
 * coordinates for the graphics that are drawn:
 *
 * <ol>
 *   <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
 *       supplied value from the preview scale to the view scale.
 *   <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
 *       coordinate from the preview's coordinate system to the view coordinate system.
 * </ol>
 */
public class GraphicOverlay extends View {
  private final Object lock = new Object();
  private int previewWidth;
  private float widthScaleFactor = 1.0f;
  private int previewHeight;
  private float heightScaleFactor = 1.0f;
  private int facing = CameraSource.CAMERA_FACING_BACK;
  private Set<Graphic> graphics = new HashSet<>();


  public abstract static class Graphic {
    private GraphicOverlay overlay;

    public Graphic(GraphicOverlay overlay) {
      this.overlay = overlay;
    }


    public abstract void draw(Canvas canvas);
    public float scaleX(float horizontal) {
      return horizontal * overlay.widthScaleFactor;
    }

    public float scaleY(float vertical) {
      return vertical * overlay.heightScaleFactor;
    }

    public Context getApplicationContext() {
      return overlay.getContext().getApplicationContext();
    }

    public float translateX(float x) {
      if (overlay.facing == CameraSource.CAMERA_FACING_FRONT) {
        return overlay.getWidth() - scaleX(x);
      } else {
        return scaleX(x);
      }
    }

    public float translateY(float y) {
      return scaleY(y);
    }

    public void postInvalidate() {
      overlay.postInvalidate();
    }
  }

  public GraphicOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void clear() {
    synchronized (lock) {
      graphics.clear();
    }
    postInvalidate();
  }

  public void add(Graphic graphic) {
    synchronized (lock) {
      graphics.add(graphic);
    }
    postInvalidate();
  }

  public void remove(Graphic graphic) {
    synchronized (lock) {
      graphics.remove(graphic);
    }
    postInvalidate();
  }

  public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
    synchronized (lock) {
      this.previewWidth = previewWidth;
      this.previewHeight = previewHeight;
      this.facing = facing;
    }
    postInvalidate();
  }

  /** Draws the overlay with its associated graphic objects. */
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (lock) {
      if ((previewWidth != 0) && (previewHeight != 0)) {
        widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
        heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
      }

      for (Graphic graphic : graphics) {
        graphic.draw(canvas);
      }
    }
  }
}
