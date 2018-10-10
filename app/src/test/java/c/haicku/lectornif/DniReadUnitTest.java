package c.haicku.lectornif;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import c.haicku.lectornif.textrecognition.DniProcessor;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DniReadUnitTest{
    @Test
    public void testReadsFile()  throws IOException {
        String[] dnisValid = {"50201633Q", "05304117H", "01187714V", "53409289T", "50849680S"};
        InputStream input = getClass().getClassLoader().getResourceAsStream("dnireads_lite.dat");
        long tic = System.currentTimeMillis();
        assertNotNull(input);

        BufferedReader reader=new BufferedReader(new InputStreamReader(input));

        DniProcessor dniProcessor = new DniProcessor();
        String dni;
        String nombreApellidos;
        String line = null;
        int nReads = 0;
        int nDniFound = 0;
        int nNombreFound = 0;
        while( (line = reader.readLine()) != null){
            if(line.length()>1 && line.substring(0,2).equals("##")){
                nReads++;
                dni = null;
            }

            //System.out.println(line);
            dni = dniProcessor.findDNI(line);
            if(dni != null){
                assertTrue(Arrays.asList(dnisValid).contains(dni));
                //System.out.println("DNI: "+dni + " found in "+line);
                nDniFound++;
            }
            nombreApellidos = dniProcessor.findNombreApellidos(line);
            if( nombreApellidos != null ){
                System.out.println("Nombre: "+nombreApellidos );
                nNombreFound++;
            }

        }
        long toc = System.currentTimeMillis();

        float percentDni =  (float) nDniFound/ nReads * 100;
        float percentNombres =  (float) nNombreFound/ nReads * 100;

        System.out.println("Processed "+nReads+" lecturas in "+(toc-tic)+" ms.");
        System.out.println("Found "+nDniFound+" números "+String.format("%.2f", percentDni) + "%");
        System.out.println("Found "+nNombreFound+" nombres "+String.format("%.2f", percentNombres) + "%");


    }
}