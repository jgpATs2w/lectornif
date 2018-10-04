package c.haicku.lectornif.textrecognition;

import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import c.haicku.lectornif.DniViewModel;
import c.haicku.lectornif.common.GraphicOverlay;

public class DniProcessor {
    private String TAG = "DniProcessor";
    private List<FirebaseVisionText.TextBlock> lecturas = new ArrayList(){};

    public boolean process(List<FirebaseVisionText.TextBlock> blocks, DniViewModel dniViewModel, GraphicOverlay graphicOverlay){

        Pattern mzr1Pattern = Pattern.compile("^[Ii]ESP(\\w{9,9})");
        Pattern dniPattern = Pattern.compile(".+\\d{8,10}\\s?[A-Z].+");
        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < blocks.size(); i++) {
            Log.d(TAG, "linea "+i);
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                String text = lines.get(j).getText();
                Log.d(TAG, text);
                Matcher mzr1Matcher = mzr1Pattern.matcher(text);
                if( mzr1Matcher.lookingAt() ){
                    Log.d(TAG + "MZR", text);
                    dniViewModel.getDni$().setValue(text);
                    Log.d(TAG + "MZRDNI", mzr1Matcher.replaceAll("$1"));

                }
                Matcher dniMatcher = dniPattern.matcher(text);
                if( dniMatcher.lookingAt()){
                    Log.d(TAG + "DNI", text);
                    dniViewModel.getDni$().setValue(text);
                    //GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, text);
                    //graphicOverlay.add(textGraphic);
                }
            }
        }
        return true;
    }
}
