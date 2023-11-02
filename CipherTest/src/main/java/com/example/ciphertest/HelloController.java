package com.example.ciphertest;

import java.io.*;
import java.lang.String;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.example.ciphertest.TestsController.incompleteGammaP;

public class HelloController {
    @FXML
    private TextArea filePath;
    @FXML
    private TextField initStateField;
    @FXML
    private TextArea originText;
    @FXML
    private TextArea cryptoText;

    private ArrayList<Integer> plainText = new ArrayList<>();

    private ArrayList<String> binary = new ArrayList<>();
    private ArrayList<String> binaryTest = new ArrayList<>();
    private ArrayList<Integer> originalText = new ArrayList<>();
    private ArrayList<Integer> finalText = new ArrayList<>();
    private ArrayList<Integer> cypherText = new ArrayList<>();

    private ArrayList<Integer> regXPol = new ArrayList<>();
    private ArrayList<Integer> regYPol = new ArrayList<>();
    private ArrayList<Integer> regZPol = new ArrayList<>();
    private String init = "";
    private String textCrypto = "";

    private String fileText = "";
    private int[] regX ;
    private int[] regY;
    private int[] regZ ;

    private int textLength = 0;

    private int regXL = 0;
    private int regYL = 0;
    private int regZL = 0;

    private int regXSyncBit = 0;
    private int regYSyncBit = 0;
    private int regZSyncBit = 0;
    private StringBuilder overallKey = new StringBuilder();

    private LFSR lfsr = new LFSR();

    @FXML
    private void load(){
        filePath.clear();
        initStateField.clear();
        originText.clear();

        cryptoText.clear();

        lfsr.clear();

        originalText.clear();
        binary.clear();
        binaryTest.clear();
        plainText.clear();
        finalText.clear();
        cypherText.clear();

        init = "";
        textCrypto = "";
        regX = new int[19];
        regY = new int[22];
        regZ = new int[23];
        textLength = 0;
        overallKey = new StringBuilder();

        lfsr.originalText.clear();
        lfsr.finalText.clear();
        lfsr.cypherText.clear();

        lfsr.init = "";
        lfsr.textCrypto = "";
        lfsr.regX = new int[19];
        lfsr.regY = new int[22];
        lfsr.regZ = new int[23];
        lfsr.textLength = 0;
        lfsr.overallKey = new StringBuilder();

    }

    private void getPolynoms(){
        fileText = filePath.getText();
        lfsr.getPolynoms(fileText);
    }

    private void readText() {
        String text = originText.getText();
        for (char ch: text.toCharArray()) {
            plainText.add((int)ch);
        }
    }

    private int getTextLength() {
        int counter = 0;
        for(String temp: binary) {
            if(!Objects.equals(temp, " ")) {
                counter++;
                binaryTest.add(temp);
            }
        }
        return counter;
    }

    private void toBinaryArray(){
        for (Integer integer : plainText) {
            String tmp = Integer.toBinaryString(integer);
            if(tmp.length() < 8){
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < (8 - tmp.length()); i++){
                    builder.append("0");
                }
                builder.append(tmp);
                binary.add(builder.toString());
            }else{
                binary.add(tmp);
            }
        }

    }

    @FXML
    private void autoFillOfInit(){
        initStateField.setText("0100111000101111010011010111110000011110101110001000101100111010");
        getPolynoms();
    }

    @FXML
    private void encrypt() throws IOException {

        readText();
        toBinaryArray();
        output(1);
        init = initStateField.getText();
        if(initCheck()){
            lfsr.textLength = textLength;
            lfsr.binary = binary;
            lfsr.cypherText.clear();
            lfsr.textCrypto = "";


            lfsr.encrypt(init);

            output(2);
            cryptoText.setText(lfsr.textCrypto);
        }

        originText.setText(lfsr.textOrig);
    }

    @FXML
    private void loadTests() throws IOException {
        if(LFSR.cypherText.size() != 0) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("tests.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Tests");
            stage.setScene(scene);
            stage.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Warning! Cypher the message.");
            alert.showAndWait();
        }

    }

    @FXML
    private void decrypt() throws IOException {
        lfsr.change();
        cryptoText.clear();
        lfsr.decrypt(init);
        output(2);
        cryptoText.setText(lfsr.textCrypto);
        /*
        change();
        init = initStateField.getText();
        if(initCheck()){
            originalText.clear();
            textCrypto = "";
            cryptoText.clear();
            finalText.clear();
            initialiseReg();
            setBinaryTest();
            getCypherText();
            output(2);
            //save();
            //makeAllClean();
        }*/
    }

    private boolean initCheck(){
        if(init.length() == 0){
            cryptoText.setText("Error! You forgot to input initial state of key!");
            return false;
        }
        initCleaner();
        if(init.length() != 64){
            cryptoText.setText("Error! Please remember, that initial states length must be 64.");
            return false;
        }
        return true;
    }

    private void initCleaner(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < init.length(); i++){
            if(init.charAt(i) == '1' || init.charAt(i) == '0'){
                builder.append(init.charAt(i));
            }
        }
        init = builder.toString();
        initStateField.setText(init);
    }

    private void output(int choose) {
        StringBuilder builder = new StringBuilder();
        if (choose == 1) {
            for (String s : binary) {
                builder.append(s).append(" ");
            }
            int a = getTextLength();
            textLength = a*8;
            //originText.setText(String.valueOf(a));
            //originText.setText(builder.toString());

        } else {
            for (Integer integer : lfsr.cypherText) {
                if(Integer.toBinaryString(integer).length() < 8){
                    for(int i = 0; i < 8 - Integer.toBinaryString(integer).length(); i++){
                        builder.append("0");
                    }
                }
                builder.append(Integer.toBinaryString(integer)).append(" ");
            }
            cryptoText.setText(lfsr.textCrypto);
            initStateField.setText(lfsr.init);
            cryptoText.setText(lfsr.overallKey.toString());
        }
    }

    private void save() throws IOException {
        String path = filePath.getText();
       /*try(FileWriter writer = new FileWriter(path, false)) {
            writer.write(textCrypto);
            writer.flush();
        }
        catch(IOException ex) {
           System.out.println(ex.getMessage());
        }*/


        File file = new File(path);
        FileOutputStream in = new FileOutputStream(file);
        for (Integer integer : cypherText) {
            in.write(integer);
        }
    }

    private void makeAllClean(){
        plainText.clear();
        binary.clear();
        cypherText.clear();
        init = "";
        overallKey = new StringBuilder();
    }
}