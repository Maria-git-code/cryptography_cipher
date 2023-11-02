package com.example.ciphertest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LFSR {

    private ArrayList<Integer> plainText = new ArrayList<>();
    private ArrayList<Integer> generatedKey = new ArrayList<>();
    public ArrayList<String> binary = new ArrayList<>();
    private ArrayList<String> binaryTest = new ArrayList<>();
    public ArrayList<Integer> originalText = new ArrayList<>();
    public ArrayList<Integer> finalText = new ArrayList<>();
    public static ArrayList<Integer> cypherText = new ArrayList<>();

    public ArrayList<Integer> regXPol = new ArrayList<>();
    public ArrayList<Integer> regYPol = new ArrayList<>();
    public ArrayList<Integer> regZPol = new ArrayList<>();
    public String init = "";
    public String textCrypto = "";

    public String textOrig = "";
    public int[] regX ;
    public int[] regY;
    public int[] regZ ;

    public int textLength = 0;

    public int regXL = 0;
    public int regYL = 0;
    public int regZL = 0;

    public int regXSyncBit = 0;
    public int regYSyncBit = 0;
    public int regZSyncBit = 0;
    public StringBuilder overallKey = new StringBuilder();

    public void clear(){

        plainText.clear();
        generatedKey.clear();
        binary.clear();
        binaryTest.clear();

        originalText.clear();
        finalText.clear();
        cypherText.clear();

        init = "";
        textCrypto = "";
        textOrig = "";
        regX = new int[19];
        regY = new int[22];
        regZ = new int[23];
        textLength = 0;
        overallKey = new StringBuilder();
    }

    public void getPolynoms(String line){
        //String line = filePath.getText();

        int counter = 1;
        StringBuilder builder = new StringBuilder();
        StringBuilder polynomials = new StringBuilder();
        for(char ch: line.toCharArray()) {
            String polynom = "";
            String p = "";
            if(ch != '\n') {
                //polynom = polynom.concat(String.valueOf(ch));
                builder.append(ch);
            }
            else {
                String o = builder.toString();

                if(!o.equals("") ) {
                    String text = builder.toString();
                    switch (counter) {
                        case (1) -> {
                            if (o.equals("0")) {
                                polynomials.append(o);
                                builder.setLength(0);
                                counter++;
                                this.regXL = 19;
                                this.regXSyncBit = 8;
                                regXPol = new ArrayList<Integer>(Arrays.asList(19, 18, 17, 14));
                            }
                            else {
                                this.regXL = getPolynomialLength(text);
                                this.regXSyncBit = getPolynomialSyncBit(text);
                                regXPol = getPolynomialIndex(text);
                            }

                        }
                        case (2) -> {
                            if (o.equals("0")) {
                                polynomials.append(o);
                                builder.setLength(0);
                                counter++;
                                this.regYL = 22;
                                this.regYSyncBit = 10;
                                regYPol = new ArrayList<Integer>(Arrays.asList(22, 21));
                            }
                            else {
                                this.regYL = getPolynomialLength(text);
                                this.regYSyncBit = getPolynomialSyncBit(text);
                                regYPol = getPolynomialIndex(text);
                            }

                        }
                        case (3) -> {
                            if (o.equals("0")) {
                                polynomials.append(o);
                                builder.setLength(0);
                                counter++;
                                this.regZL = 23;
                                this.regZSyncBit = 10;
                                regZPol = new ArrayList<Integer>(Arrays.asList(23, 22, 21, 8));
                            }
                            else {
                                this.regZL = getPolynomialLength(text);
                                this.regZSyncBit = getPolynomialSyncBit(text);
                                regZPol = getPolynomialIndex(text);
                            }

                        }
                    }
                    if (!o.equals("0")) {
                        String a = setPolynomials(builder);
                        polynomials.append(a);
                        builder.setLength(0);
                        counter++;
                    }

                }

            }


        }
        //originText.setText(polynomials.toString());
        //originText.setText(regXPol + "___" + regYPol + "___" + regZPol);
    }

    private String setPolynomials(StringBuilder builder){
        String line = builder.toString();
        int polynomialLength = getPolynomialLength(line);
        ArrayList<Integer> polynomialValues = getPolynomialIndex(line);
        int syncBit = getPolynomialSyncBit(line);
        String a = "";
        a = a + "___" + polynomialLength + "___" + polynomialValues + "___" + syncBit + "\n";
        return a;
    }

    private int getPolynomialLength(String builder){
        int index = builder.indexOf("+");
        String line = builder.substring(1, index);
        return Integer.parseInt(line);
    }

    private ArrayList<Integer> getPolynomialIndex(String builder){
        int index = builder.lastIndexOf("+");
        String line = builder.substring(1, index);
        line = line.replace("+", "");
        char[] text = line.toCharArray();
        String number = "";
        ArrayList<Integer> indexes = new ArrayList<>();
        int counter = 0;
        for(char c: text) {
            String a = "";

            if(c != 'x') {
                number = number.concat(String.valueOf(c));
                counter++;
            }
            else{
                indexes.add(Integer.valueOf(number));
                number = "";
                counter++;
            }
            if(counter == text.length) {
                if(!number.equals("")) {
                    indexes.add(Integer.valueOf(number));
                }

            }
        }
        if(text[text.length-1] == 'x') {
            indexes.add(1);
        }

        return indexes;
    }

    private int getPolynomialSyncBit(String builder){
        int index = builder.lastIndexOf(" ");
        String line = builder.substring(index+1);
        return Integer.parseInt(line);
    }

    public void encrypt(String key) throws IOException {
        init = key;

            initialiseReg(key);
            setBinaryTest();
            getCypherText();
    }

    private void initialiseReg(String key){
        regX = new int[regXL];
        regY = new int[regYL];
        regZ = new int[regZL];
        Arrays.fill(regX, 0);
        Arrays.fill(regY, 0);
        Arrays.fill(regZ, 0);

        char[] arr = key.toCharArray();
        int[] initArr = new int[arr.length];
        for(int i = 0; i < arr.length; i++){
            initArr[i] = Character.getNumericValue(arr[i]);
        }

        int[] newArrayX = new int[regX.length];
        int[] newArrayY = new int[regY.length];
        int[] newArrayZ = new int[regZ.length];

        int[] testArrayX = new int[regX.length];
        int[] testArrayY = new int[regY.length];
        int[] testArrayZ = new int[regZ.length];

        Arrays.fill(testArrayX, 0);
        Arrays.fill(testArrayY, 0);
        Arrays.fill(testArrayZ, 0);

        for(int count = 0; count < 64; count++){
            int temp = 0;
            int leastBitY = 0;
            int leastBitZ = 0;
            for(int i: regXPol) {
                temp = temp^testArrayX[regXL-i];
            }
            for(int i: regYPol) {
                leastBitY = leastBitY^testArrayY[regYL-i];
            }
            for(int i: regZPol) {
                leastBitZ = leastBitZ^testArrayZ[regZL-i];
            }

            int leastBitX = (temp^initArr[count]);
            leastBitY = leastBitY^initArr[count];
            leastBitZ = leastBitZ^initArr[count];

            for(int i = 0, k = 1; k< testArrayX.length; i++, k++){
                newArrayX[i] = testArrayX[k];
            }
            for(int i = 0, k = testArrayX.length-1; k<testArrayX.length; i++, k++){
                newArrayX[k] = testArrayX[i];
            }
            newArrayX[regXL-1] = (char)leastBitX;
            testArrayX = newArrayX;

            for(int i = 0, k = 1; k< testArrayY.length; i++, k++){
                newArrayY[i] = testArrayY[k];
            }
            for(int i = 0, k = testArrayY.length-1; k<testArrayY.length; i++, k++){
                newArrayY[k] = testArrayY[i];
            }
            newArrayY[regYL-1] = (char)leastBitY;
            testArrayY = newArrayY;

            for(int i = 0, k = 1; k< testArrayZ.length; i++, k++){
                newArrayZ[i] = testArrayZ[k];
            }
            for(int i = 0, k = testArrayZ.length-1; k<testArrayZ.length; i++, k++){
                newArrayZ[k] = testArrayZ[i];
            }
            newArrayZ[regZL-1] = (char)leastBitZ;
            testArrayZ = newArrayZ;

        }

        for(int count = 0; count < 100; count++){
            int maj = major(testArrayX[regXL-regXSyncBit-1], testArrayY[regYL-regYSyncBit-1], testArrayZ[regZL-regZSyncBit-1]);

            if (testArrayX[regXL-regXSyncBit-1] == maj) {
                int leastBitX = 0;
                for(int i: regXPol) {
                    leastBitX = leastBitX^testArrayX[regXL-i];
                }
                //int leastBitX = ((testArrayX[5]^testArrayX[2])^testArrayX[1])^testArrayX[0];

                for(int i = 0, k = 1; k< testArrayX.length; i++, k++){
                    newArrayX[i] = testArrayX[k];
                }
                for(int i = 0, k = testArrayX.length-1; k<testArrayX.length; i++, k++){
                    newArrayX[k] = testArrayX[i];
                }
                newArrayX[regXL-1] = (char)leastBitX;
                testArrayX = newArrayX;
            }

            if (testArrayY[regYL-regYSyncBit-1] == maj) {
                int leastBitY = 0;
                for(int i: regYPol) {
                    leastBitY = leastBitY^testArrayY[regYL-i];
                }

                //int leastBitY = (testArrayY[0]^testArrayY[1]);

                for(int i = 0, k = 1; k< testArrayY.length; i++, k++){
                    newArrayY[i] = testArrayY[k];
                }
                for(int i = 0, k = testArrayY.length-1; k<testArrayY.length; i++, k++){
                    newArrayY[k] = testArrayY[i];
                }
                newArrayY[regYL-1] = (char)leastBitY;
                testArrayY = newArrayY;
            }

            if (testArrayZ[regZL-regZSyncBit-1] == maj) {
                int leastBitZ = 0;
                for(int i: regZPol) {
                    leastBitZ = leastBitZ^testArrayZ[regZL-i];
                }

                //int leastBitZ = ((testArrayZ[15]^testArrayZ[2])^testArrayZ[1])^testArrayZ[0];

                for(int i = 0, k = 1; k< testArrayZ.length; i++, k++){
                    newArrayZ[i] = testArrayZ[k];
                }
                for(int i = 0, k = testArrayZ.length-1; k<testArrayZ.length; i++, k++){
                    newArrayZ[k] = testArrayZ[i];
                }
                newArrayZ[regZL-1] = (char)leastBitZ;
                testArrayZ = newArrayZ;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int value : testArrayY) {
            builder.append(value);
        }
        //keyField.setText(builder.toString());

        regX = testArrayX;
        regY = testArrayY;
        regZ = testArrayZ;
    }

    int major(int x ,int y,int  z){
        int m;
        if(x == 0){
            if(y == 0 || z == 0){
                m = 0;
            }else{
                m = 1;
            }
        }else{
            if(y == 1 || z == 1){
                m = 1;
            }else{
                m = 0;
            }
        }
        return m;
    }

    private void setBinaryTest() {
        for(String temp: binary) {
            char[] strToArray = temp.toCharArray();
            for(char c : strToArray) {
                int a = Character.getNumericValue(c);
                originalText.add(a);
            }
        }

    }

    private void getCypherText(){
        int[] newArrayX = new int[regXL];
        int[] newArrayY = new int[regYL];
        int[] newArrayZ = new int[regZL];

        int[] testArrayX = regX;
        int[] testArrayY = regY;
        int[] testArrayZ = regZ;

        for(int c = 0; c < textLength; c++) {

            int maj = major(testArrayX[regXL-regXSyncBit-1], testArrayY[regYL-regYSyncBit-1], testArrayZ[regZL-regZSyncBit-1]);
            int tX = -1, tY = -1, tZ = -1;


            if (testArrayX[regXL-regXSyncBit-1] == maj) {
                int leastBitX = 0;
                for(int i: regXPol) {
                    leastBitX = leastBitX^testArrayX[regXL-i];
                }

                //int leastBitX = ((testArrayX[5]^testArrayX[2])^testArrayX[1])^testArrayX[0];
                tX = testArrayX[0];
                for(int i = 0, k = 1; k< testArrayX.length; i++, k++){
                    newArrayX[i] = testArrayX[k];
                }
                for(int i = 0, k = testArrayX.length-1; k<testArrayX.length; i++, k++){
                    newArrayX[k] = testArrayX[i];
                }
                newArrayX[regXL-1] = (char)leastBitX;
                testArrayX = newArrayX;
            }

            if (testArrayY[regYL-regYSyncBit-1] == maj) {
                int leastBitY = 0;
                for(int i: regYPol) {
                    leastBitY = leastBitY^testArrayY[regYL-i];
                }

                //int leastBitY = (testArrayY[0]^testArrayY[1]);
                tY = testArrayY[0];
                for(int i = 0, k = 1; k< testArrayY.length; i++, k++){
                    newArrayY[i] = testArrayY[k];
                }
                for(int i = 0, k = testArrayY.length-1; k<testArrayY.length; i++, k++){
                    newArrayY[k] = testArrayY[i];
                }
                newArrayY[regYL-1] = (char)leastBitY;
                testArrayY = newArrayY;
            }

            if (testArrayZ[regZL-regZSyncBit-1] == maj) {
                int leastBitZ = 0;
                for(int i: regZPol) {
                    leastBitZ = leastBitZ^testArrayZ[regZL-i];
                }

                //int leastBitZ = ((testArrayZ[15]^testArrayZ[2])^testArrayZ[1])^testArrayZ[0];
                tZ = testArrayZ[0];
                for(int i = 0, k = 1; k< testArrayZ.length; i++, k++){
                    newArrayZ[i] = testArrayZ[k];
                }
                for(int i = 0, k = testArrayZ.length-1; k<testArrayZ.length; i++, k++){
                    newArrayZ[k] = testArrayZ[i];
                }
                newArrayZ[regZL-1] = (char)leastBitZ;
                testArrayZ = newArrayZ;
            }

            int genK = 0;
            if (tX != -1) {

                if(tY != -1){
                    genK = tX^tY;
                    if (tZ != -1) {
                        genK = genK^tZ;
                    }
                }

                if(tY == -1) {
                    genK = tX^tZ;
                }
            }
            else {
                genK = tY^tZ;
            }
            generatedKey.add(genK);
            int temp = (originalText.get(c))^genK;
            finalText.add(temp);
        }
        StringBuilder builder = new StringBuilder();
        //keyField.setText(String.valueOf(generatedKey));
        for(int i: finalText) {
            builder.append(i);
            cypherText.add(i);
        }
        //cryptoText.setText(String.valueOf(builder));


        String s = builder.toString();
        String a = "";
        for (int index = 0; index < s.length(); index+=8) {
            String temp = s.substring(index, index+8);
            int num = Integer.parseInt(temp,2);
            char letter = (char) num;
            a = a+letter;
        }
        textCrypto = a;
        /*char[] fByteArr = fByte.toCharArray();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < fByteArr.length; i++){
            int tmp = Character.getNumericValue(fByteArr[i]) ^ Character.getNumericValue(keyArr[i]);
            builder.append(tmp);
        }
        int value = Integer.parseInt(builder.toString(), 2);
        cypherText.add(value);*/
    }

     void decrypt(String key) throws IOException {

        change();

            originalText.clear();
            textCrypto = "";
            //cryptoText.clear();
            finalText.clear();
            initialiseReg(key);
            setBinaryTest();
            getCypherText();


    }

    public void change() {
        binary.clear();
        int temp = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cypherText.size(); i++) {
            builder.append(cypherText.get(i));
            temp++;
            if(temp == 8) {
                binary.add(String.valueOf(builder));
                builder.setLength(0);
                temp = 0;
            }
        }
        //cryptoText.clear();
    }



}
