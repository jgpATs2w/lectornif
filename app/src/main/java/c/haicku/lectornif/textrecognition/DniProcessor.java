package c.haicku.lectornif.textrecognition;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import c.haicku.lectornif.LivePreviewActivity;

public class DniProcessor {
    private String TAG = "DniProcessor";
    private List<FirebaseVisionText.TextBlock> lecturas = new ArrayList(){};
    private File outputFile;
    private Pattern dniPattern;
    private Pattern mrz1EspPattern;
    private Pattern mrz1FraPattern;
    private Pattern mzr2Pattern;
    private Pattern mzr3Pattern;

    private final String NEW_LINE = "\r\n";
    private final String MZR_SEPARATOR = "[<k]";

    public DniProcessor(){
        initPatterns();
    }
    public DniProcessor(File testFile){
        this.outputFile = testFile;
        initPatterns();
    }

    private void initPatterns(){
        dniPattern = Pattern.compile(       "(\\d{8,8})([a-z])",                                                    Pattern.CASE_INSENSITIVE);
        mrz1EspPattern = Pattern.compile(   "i?desp\\w+\\d{8,8}[a-z]("+MZR_SEPARATOR+")+",                           Pattern.CASE_INSENSITIVE);
        mrz1FraPattern = Pattern.compile(   "i?dfra\\w+\\d{15,15}"+MZR_SEPARATOR,                                   Pattern.CASE_INSENSITIVE);
        mzr2Pattern = Pattern.compile(      "([a-z]+)"+MZR_SEPARATOR+"+",                                         Pattern.CASE_INSENSITIVE);
        mzr3Pattern = Pattern.compile(      "([a-z]+"+MZR_SEPARATOR+")+",Pattern.CASE_INSENSITIVE);
    }

    public Dni process( List<FirebaseVisionText.TextBlock> blocks ) {
        Dni dni = new Dni();
        try {
            FileWriter fileWriter = null;
            if(outputFile != null )
                fileWriter = new FileWriter(outputFile, true);

            for (int i = 0; i < blocks.size(); i++) {
                List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                for (int j = 0; j < lines.size(); j++) {
                    if(i == 0){
                        if(LivePreviewActivity.recordData)
                            if(fileWriter != null )
                                fileWriter.write("########################"+NEW_LINE);
                        Log.d(TAG, "#########################");
                    }
                    FirebaseVisionText.Line line = lines.get(j);
                    String text = line.getText();
                    float confidence = 0;
                    int top = 0;
                    int bottom = 0;
                    int left = 0;
                    int right = 0;
                    try{
                        confidence = line.getConfidence();
                        top = line.getBoundingBox().top;
                        bottom = line.getBoundingBox().bottom;
                        left = line.getBoundingBox().left;
                        right = line.getBoundingBox().right;
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }


                    Log.d(TAG, text);
                    if(LivePreviewActivity.recordData)
                        fileWriter.write(text+","+
                                confidence + ","+top +","+bottom +","+left +","+right +NEW_LINE);

                    dni.numero = findDNI(text);
                }
            }
            if(fileWriter != null )
                fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dni;
    }

    public String findDNI(String lineText){
        Matcher dniMatcher = dniPattern.matcher( lineText );
        String numero = null;

        if( dniMatcher.find()){
            if(checkLetraDNI(dniMatcher.group(1), dniMatcher.group(2)))
                numero =  dniMatcher.group(1) + dniMatcher.group(2);
        }
        return numero;
    }
    public String findCNIFrance(String lineText){
        Matcher dniMatcher = dniPattern.matcher( lineText );
        String numero = null;

        if( dniMatcher.find()){
            if(checkLetraDNI(dniMatcher.group(1), dniMatcher.group(2)))
                numero =  dniMatcher.group(1) + dniMatcher.group(2);
        }
        return numero;
    }
    public String findNombreApellidos(String lineText){
        Matcher matcher = mzr3Pattern.matcher( lineText );
        String nombreApellidos = null;

        if( matcher.find()){
            nombreApellidos = matcher.group(1);
            nombreApellidos = nombreApellidos.replaceAll(MZR_SEPARATOR+"+", "");
        }
        return nombreApellidos;
    }

    private boolean checkLetraDNI(@Nullable String dni, String letra) {
        int miDNI = Integer.parseInt( dni.substring(0,8) );
        int resto = 0;

        String[] asignacioNEW_LINEetra = {"T", "R", "W", "A", "G", "M", "Y", "F", "P", "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C", "K", "E"};

        resto = miDNI % 23;

        return asignacioNEW_LINEetra[resto].equals(letra);
    }
}
