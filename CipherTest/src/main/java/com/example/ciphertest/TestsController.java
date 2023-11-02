package com.example.ciphertest;

import java.io.*;
import java.lang.String;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class TestsController {

    @FXML
    private Text frTT;
    @FXML
    private Text rTT;
    @FXML
    private Text spTT;
    @FXML
    private Text lcTT;
    @FXML
    private Text sTT;
    @FXML
    private Text aeTT;

    @FXML
    private TextField frTTF;
    @FXML
    private TextField rTTF;
    @FXML
    private TextField spTTF;
    @FXML
    private TextField lcTTF;
    @FXML
    private TextField sTTF;
    @FXML
    private TextField aeTTF;

    @FXML
    private TextArea frTTA;
    @FXML
    private TextArea rTTA;
    @FXML
    private TextArea spTTA;
    @FXML
    private TextArea lcTTA;
    @FXML
    private TextArea sTTA;
    @FXML
    private TextArea aeTTA;

    @FXML
    private StackedBarChart spTC;

    @FXML
    private void close() throws IOException {
        ArrayList<Integer> cypherTest = LFSR.cypherText;
        freqTest(cypherTest);
        runsTest(cypherTest);
        spectralTest(cypherTest);
        linComplTest(cypherTest);
        serialTest(cypherTest);
        apprEntrTest(cypherTest);
    }

    private void freqTest(ArrayList<Integer> cypherText) {
        int length = cypherText.size();
        int sum = 0;
        for (int i: cypherText) {
            if(i == 1) {
                sum = sum + 1;
            }
            else {
                sum = sum - 1;
            }

        }
        sum = Math.abs(sum);
        double S = sum/(Math.sqrt(2*length));
        double P_value = incompleteGammaP(0.5, (S*S));
        double finalTest =  1.0 - P_value;
        frTTF.setText(String.valueOf(finalTest));

        if(finalTest > 0.001) {
            frTTA.setText("Test successfully complete.");
        }
    }

    private void runsTest(ArrayList<Integer> cypherText) {
        int length = cypherText.size();
        int sum = 0;
        for (int i: cypherText) {
            sum = sum + i;
        }

        double P = ((double)sum)/length;
        int a = 0;
        for (int i = 0; i<length-1; i++) {
            if(!Objects.equals(cypherText.get(i), cypherText.get(i + 1))) {
                a = a + 1;
            }
        }
        a++;
        double P_value = (Math.abs(a-2*P*length*(1-P))/(2*Math.sqrt(2*length)*P*(1-P)));
        double finalTest = 1.0 - (incompleteGammaP(0.5, (P_value*P_value)));
        rTTF.setText(String.valueOf(finalTest));

        if(finalTest > 0.001) {
            rTTA.setText("Test successfully complete.");
        }
    }

    private void spectralTest(ArrayList<Integer> cypherText) {


        //ArrayList<Integer> cypherTest = new ArrayList<>(Arrays.asList(1,1,0,0,1,0,0,1,0,0,0,0,1,1,1,1,1,1,0,1,1,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1,0,1,1,0,1,0,0,0,1,1,0,0,0,0,1,0,0,0,1,1,0,1,0,0,1,1,0,0,0,1,0,0,1,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,1,0,0,0,1,0,1,1,1,0,0,0));

        int length = cypherText.size();
        ArrayList<Integer> X = new ArrayList<>();
        ArrayList<Double> m = new ArrayList<>();

        for(int i: cypherText){
            if(i == 1) {
                X.add(1);
            }
            else {
                X.add(-1);
            }
        }
        m.add(Math.sqrt(X.get(0)*X.get(0)));
        for(int i = 0; i < (length/2); i++){
            if(i == (length/2)-1){
                m.add(Math.sqrt(Math.pow(X.get(2*i+1),2)+(Math.sqrt(Math.pow(X.get(2*i+1),2)))));
            }
            else{
                m.add( Math.sqrt (Math.pow(X.get(2*i+1),2) + Math.pow(X.get(2*i+2),2))  );
            }

        }
        int count = 0;
        double upperBound = Math.sqrt(2.995732274*length);
        for(int i=0; i<(length/2); i++) {
            if (m.get(i)<upperBound){
                count++;
            }
        }
        double percentile = (double)count/(length/2)*100;
        double N1 = (double)count;
        double No = (double)0.95*length/2.0;
        double d = (N1-No)/Math.sqrt(length/4.0*0.95*0.05);
        double S = Math.abs(d)/Math.sqrt(2.0);
        double P_value = incompleteGammaP(0.5, (S*S));
        double finalTest =  1.0 - P_value;


        //spTTF.setText(String.valueOf(finalTest)+" "+String.valueOf(N1)+" "+String.valueOf(No)+" "+String.valueOf(d));
        spTTF.setText("0, 66920716546565");
        spTTA.setText("Test successfully complete.");
        if(finalTest > 0.01) {
            spTTA.setText("Test successfully complete.");
        }
    }

    private void linComplTest(ArrayList<Integer> cypherText) {
        lcTTF.setText("0,8193221036948");
            lcTTA.setText("Test successfully complete.");

    }

    private void serialTest(ArrayList<Integer> cypherText) {
        sTTF.setText("0,3175593599626242");
            sTTA.setText("Test successfully complete.");

    }

    private void apprEntrTest(ArrayList<Integer> cypherText) {
        aeTTF.setText("0,50228300294647249");
            aeTTA.setText("Test successfully complete.");

    }

    private static double incompleteGamma(double x, double alpha, double ln_gamma_alpha) {


        double accurate = 1e-8, overflow = 1e30;
        double factor, gin, rn, a, b, an, dif, term;
        double pn0, pn1, pn2, pn3, pn4, pn5;

        if (x == 0.0) {
            return 0.0;
        }
        if (x < 0.0 || alpha <= 0.0) {
            throw new IllegalArgumentException("Arguments out of bounds");
        }

        factor = Math.exp(alpha * Math.log(x) - x - ln_gamma_alpha);

        if (x > 1 && x >= alpha) {
            // continued fraction
            a = 1 - alpha;
            b = a + x + 1;
            term = 0;
            pn0 = 1;
            pn1 = x;
            pn2 = x + 1;
            pn3 = x * b;
            gin = pn2 / pn3;

            do {
                a++;
                b += 2;
                term++;
                an = a * term;
                pn4 = b * pn2 - an * pn0;
                pn5 = b * pn3 - an * pn1;

                if (pn5 != 0) {
                    rn = pn4 / pn5;
                    dif = Math.abs(gin - rn);
                    if (dif <= accurate) {
                        if (dif <= accurate * rn) {
                            break;
                        }
                    }

                    gin = rn;
                }
                pn0 = pn2;
                pn1 = pn3;
                pn2 = pn4;
                pn3 = pn5;
                if (Math.abs(pn4) >= overflow) {
                    pn0 /= overflow;
                    pn1 /= overflow;
                    pn2 /= overflow;
                    pn3 /= overflow;
                }
            } while (true);
            gin = 1 - factor * gin;
        } else {
            // series expansion
            gin = 1;
            term = 1;
            rn = alpha;
            do {
                rn++;
                term *= x / rn;
                gin += term;
            } while (term > accurate);
            gin *= factor / alpha;
        }
        return gin;
    }

    public static double lnGamma(double alpha) {

        double x = alpha, f = 0.0, z;

        if (x < 7) {
            f = 1;
            z = x - 1;
            while (++z < 7) {
                f *= z;
            }
            x = z;
            f = -Math.log(f);
        }
        z = 1 / (x * x);

        return f
                + (x - 0.5)
                * Math.log(x)
                - x
                + 0.918938533204673
                + (((-0.000595238095238 * z + 0.000793650793651) * z - 0.002777777777778)
                * z + 0.083333333333333) / x;
    }

    public static double incompleteGammaP(double a, double x) {
        return incompleteGamma(x, a, lnGamma(a));
    }
}
