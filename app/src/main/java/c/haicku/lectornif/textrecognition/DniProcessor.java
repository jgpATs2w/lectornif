package c.haicku.lectornif.textrecognition;

import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DniProcessor {
    private String TAG = "DniProcessor";
    private List<FirebaseVisionText.TextBlock> lecturas = new ArrayList(){};

    public boolean process( List<FirebaseVisionText.TextBlock> blocks ){

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                String text = lines.get(j).getText();
                if( text.matches("<+"))
                    Log.d(TAG, lines.get(j).toString());
            }
        }
        return true;
    }
}
