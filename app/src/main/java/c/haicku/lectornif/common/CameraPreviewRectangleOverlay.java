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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;


public class CameraPreviewRectangleOverlay extends View {
  private final Object lock = new Object();
  private int previewWidth;
  private float widthScaleFactor = 1.0f;
  private int previewHeight;
  private float heightScaleFactor = 1.0f;
  private int facing = CameraSource.CAMERA_FACING_BACK;
  private static final float BOX_STROKE_WIDTH = 5.0f;


  public static final double DNI_WIDTH = 85.60;
  public static final double DNI_HEIGHT = 53.98;
  public static final int MARGIN = 10;

  private float left;
  private float top;
  private float right;
  private float bottom;


  public CameraPreviewRectangleOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
    synchronized (lock) {
      this.previewWidth = previewWidth;
      this.previewHeight = previewHeight;
      this.facing = facing;
    }
    postInvalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    synchronized (lock) {
      if ((previewWidth != 0) && (previewHeight != 0)) {
        widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
        heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
      }

      Paint boxPaint = new Paint();
      boxPaint.setColor(Color.RED);
      boxPaint.setStyle(Paint.Style.STROKE);
      boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
      left =  MARGIN;
      top = ( canvas.getHeight() - (float) (DNI_HEIGHT / DNI_WIDTH) * ( canvas.getWidth() - 2* MARGIN ) ) / 2;
      right = canvas.getWidth() - MARGIN;
      bottom = ( canvas.getHeight() + (float) (DNI_HEIGHT / DNI_WIDTH) * ( canvas.getWidth() - 2* MARGIN ) ) / 2;

      canvas.drawRect(left, top, right, bottom, boxPaint);
    }
  }

  public float getRectangleLeft() {
    return left;
  }

  public float getRectangleTop() {
    return top;
  }

  public float getRectangleRight() {
    return right;
  }

  public float getRectangleBottom() {
    return bottom;
  }

  public float[] getRectangleCorners(){
    return new float[]{top, right, bottom,left};
  }
}
