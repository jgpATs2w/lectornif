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
package c.haicku.lectornif.textrecognition;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import c.haicku.lectornif.Dni;
import c.haicku.lectornif.DniViewModel;
import c.haicku.lectornif.common.CameraPreviewRectangleOverlay;
import c.haicku.lectornif.common.VisionProcessorBase;
import c.haicku.lectornif.common.FrameMetadata;
import c.haicku.lectornif.common.GraphicOverlay;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** Processor for the text recognition demo. */
public class TextRecognitionProcessor extends VisionProcessorBase<FirebaseVisionText> {

  private static final String TAG = "TextRecProc";

  private DniProcessor dniProcessor;

  private final FirebaseVisionTextRecognizer detector;
  private TextView textView;
  private File testFile;
  private CameraPreviewRectangleOverlay rectangleOverlay;

  public TextRecognitionProcessor(TextView textView, File testFile, CameraPreviewRectangleOverlay rectangleOverlay) {
    detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    dniProcessor  = new DniProcessor(testFile);
    this.rectangleOverlay = rectangleOverlay;
    this.textView = textView;
    this.testFile = testFile;
  }

  @Override
  public void stop() {
    try {
      detector.close();
    } catch (IOException e) {
      Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
    }
  }

  @Override
  protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
    return detector.processImage(image);
  }

  @Override
  protected void onSuccess(
      @NonNull FirebaseVisionText results,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {
    graphicOverlay.clear();
    List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
    boolean firstRead = true;

    for (int i = 0; i < blocks.size(); i++) {
      List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
      for (int j = 0; j < lines.size(); j++) {
        FirebaseVisionText.Line line = lines.get(j);
        Rect lineRect = line.getBoundingBox();
        float[] referenceCorners = rectangleOverlay.getRectangleCorners();
        if(lineRect.top < referenceCorners[0] || lineRect.bottom > referenceCorners[2])
          continue;

        if(firstRead){
          dniProcessor.startReads(referenceCorners);
          firstRead = false;
        }
        Dni dni = dniProcessor.process( line);

        if(dni != null && dni.getNumero() != null)
          textView.setText(dni.getNumero());

        List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
        for (int k = 0; k < elements.size(); k++) {
          GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k));
          graphicOverlay.add(textGraphic);
        }
      }

      dniProcessor.endReads();
    }


  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.w(TAG, "Text detection failed." + e);
  }
}
