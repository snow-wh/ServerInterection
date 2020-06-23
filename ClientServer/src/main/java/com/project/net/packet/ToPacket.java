package com.project.net.packet;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ToPacket {

    @JsonProperty("Code")
    private String code;
    @JsonProperty("ID")
    private int id;
    @JsonProperty("Length")
    private int length;
    @JsonProperty("Date")
    private String date;


    public ToPacket(){}

    public ToPacket(String code, int id, String date){

        this.code = code;
        this.id = id;
        this.length = date.length();
        this.date = date;

    }



    public String Convert (ToPacket packet) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonObject = mapper.writeValueAsString(packet);
        return jsonObject;
    }

    @JsonCreator
    public ToPacket(

            @JsonProperty("Code") String code,
                @JsonProperty("ID") int id,
                    @JsonProperty("Length") int length,
                        @JsonProperty("Date") String date){
            this.code = code;
            this.id = id;
            this.length = length;
            this.date = date;
    }

    public String GetDate(){
        return date;
    }

    public void  SetDate(String date){
        this.date = date;
    }

    public  String GetCode(){
        return code;
    }

    public void SetCode (String code){
        this.code = code;
    }

    public int GetID(){
        return  id;
    }

    public void SetID(int id){
        this.id = id;
    }

    public int GetLength(){
        return  length;
    }

    public void SetLength(int length){
        this.length = length;
    }



}
