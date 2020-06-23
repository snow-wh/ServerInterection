package com.project.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.net.connect.ConnectionDB;
import com.project.net.packet.ToPacket;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class MHAP {

    private boolean dbCheck = false;

    private String code = "" ;
    private int id  ;
    private int length ;
    private String data ;

    private String dataCash;
    private String dataCashSalt;
    private int idConnection;

    private boolean status;


    private static ToPacket packet = new ToPacket();


    public MHAP(){}

    public MHAP (String code, int id, int length, String data){

        this.code = code;
        this.id = id;
        this.length = length;
        this.data = data;

    }



    private boolean DBCheck (){

        ConnectionDB connectionDB = new ConnectionDB();

        if(connectionDB.GetID(data)==0)
            dbCheck = false;
        else
            dbCheck=true;

        return dbCheck;
    }


    public int GetID(){
        ConnectionDB connectionDB = new ConnectionDB();
        return connectionDB.GetID(data);
    }
    protected void CreateSalt(){
        ConnectionDB connectionDB = new ConnectionDB();
        connectionDB.SetSalt(data,idConnection);
    }
    public void GetIDConnection(int id){
        idConnection = id;
    }

    private String GetHash(){
        ConnectionDB connectionDB = new ConnectionDB();
        String str = connectionDB.GetHas(idConnection);
        String str2 = connectionDB.GetSalt(idConnection);
        String str3 = str+str2;
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = md5.digest(str3.getBytes());
        StringBuilder builderHas = new StringBuilder();
        for (byte b: bytes) {
            builderHas.append(String.format("%02X",b));
        }
        String str4 = builderHas.toString();
        return str4;
    }
    public int GetPacketID(){
        return id;
    }
    public boolean GetStatus(){
        if (code.equals("F2") || code.equals("F4"))
            status=true;
        if (code.equals("F5"))
            status=false;
        return status;
    }



    public ToPacket CreatePacket() throws JsonProcessingException {

       switch (code){
            case "F1" :
                if (DBCheck() == true){
                    packet = CreatePacketChallenge();

                }else {
                    packet = CreatePacketFailure();

                }
                break;
            case "F2" :
                status=true;
                dataCashSalt = data;
                packet = CreatePacketReply();
                break;
            case "F3" :
                String str = data;
                String str1 = GetHash();
                if (str.equals(str1)){
                    packet = CreatePacketSuccess();
                    data=null;
                    CreateSalt();
                }else {
                    packet = CreatePacketFailure();
                    data=null;
                    CreateSalt();
                }

                break;
           case "F4":
               status = true;
               break;
           case "F5":
               status = false;
               break;
            default: packet = CreatePacketSmac();
        }

        return packet;
    }

    private ToPacket CreatePacketSmac() throws JsonProcessingException {

        code = "F1";
        length = 56;
        data = MacDate();
        id = 0;

        packet.SetCode(code);
        packet.SetID(id);
        packet.SetLength(length);
        packet.SetDate(data);


        return packet;

    }
    private ToPacket CreatePacketFailure(){

        code = "F5";
        length = 56;
        data = FailureDate();


        packet.SetCode(code);
        packet.SetID(++id);
        packet.SetLength( length );
        packet.SetDate(data);

        return packet;
    }
    private ToPacket CreatePacketChallenge(){

        code = "F2";
        length = 56;
        data = ChallengeDate();

        packet.SetCode("F2");
        packet.SetID(++id);
        packet.SetLength( 56 );
        packet.SetDate(data);

        CreateSalt();

        return packet;
    }
    private ToPacket CreatePacketReply(){

        packet.SetCode("F3");
        packet.SetID(++id);
        packet.SetLength( 56 );
        packet.SetDate(ReplyDate());

        return packet;
    }
    private ToPacket CreatePacketSuccess(){

        packet.SetCode("F4");
        packet.SetID(++id);
        packet.SetLength( 56 );
        packet.SetDate(SuccessDate());

        return packet;
    }



    private String ChallengeDate()  {

        int minSum = 48;
        int maxSum = 122;
        int minValue = 5;
        int maxValue =  10;

        int wordLength = (int)(Math.random()*(maxValue-minValue)+minValue);
        StringBuilder builder = new StringBuilder();
        for (int j=0; j<wordLength; j++) {
            builder.append((char) ((int) ((Math.random() * (maxSum - minSum)) + minSum)));
        }


        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes = md5.digest(builder.toString().getBytes());
        StringBuilder builderHas = new StringBuilder();
        for (byte b: bytes) {
            builderHas.append(String.format("%02X",b));
        }

        return builderHas.toString();
    }

    public void SetData(String data){
        this.dataCash=data;
    }

    private String ReplyDate(){

        String date1 = dataCash;
        String date2 = dataCashSalt;

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] bytes1 = md5.digest(dataCash.getBytes());
        StringBuilder builderHas1 = new StringBuilder();
        for (byte b: bytes1) {
            builderHas1.append(String.format("%02X",b));
        }
        String str = builderHas1.toString() + dataCashSalt;
        byte[] bytes = md5.digest(str.getBytes());
        StringBuilder builderHas = new StringBuilder();
        for (byte b: bytes) {
            builderHas.append(String.format("%02X",b));
        }
        data = builderHas.toString();
        return data;
    }

    private String SuccessDate(){
        return "OK";
    }

    private String FailureDate(){
        return "Fail";
    }



    private String MacDate(){

        String command = "GETMAC";

        String finalMac ="";

        Pattern p = Pattern.compile("([a-fA-F0-9]{1,2}(-|:)){5}[a-fA-F0-9]{1,2}");
        try {
            Process pa = Runtime.getRuntime().exec(command);
            pa.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    pa.getInputStream()));


            Matcher m;
            while ((finalMac = reader.readLine()) != null) {

                m = p.matcher(finalMac);

                if (!m.find())
                    continue;
                finalMac = m.group();
                break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalMac;
    }

}


