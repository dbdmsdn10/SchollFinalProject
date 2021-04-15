package com.example.schoolfinalproject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Parsing {
//		System.out.println("확인용");
    public void Parsing(InputStream inputStream){
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        try {
            String data = null;
            int i=inputStream.read();
            while(i!=-1){
                b.write(i);
                i=inputStream.read();
            }
            data=new String(b.toByteArray(),"utf-8");
            inputStream.close();

            String data2[]=data.split("\n");
            String before="";
            for(int k=0;k<data2.length;k++){
                String par[]=data2[k].split("	");//0 날짜 1 시간 2코드 3 혈당
                String date[]=par[0].split("-");//0은 날짜, 1은 월, 2는 연도
                String hour[]=par[1].split(":");//0은 시간 1은 분
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.parseInt(date[2]));
                cal.set(Calendar.MONTH, Integer.parseInt(date[1]));
                cal.set(Calendar.DATE, Integer.parseInt(date[0]));
                cal.set(Calendar.HOUR, Integer.parseInt(hour[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(hour[1]));

                int code=Integer.parseInt(par[2]);
                String stcode = null;
                if(32<code&&code<36) {
                    stcode="인슐인 이후";
                } else if(48==code||code==47||code==72) {
                    stcode="정기 측정	";
                }
                else if(58==code||code==60||code==62||code==64) {
                    stcode="식전	";
                }else if(59==code||code==61||code==63&code==66||code==67||code==68) {
                    stcode="식후		";
                }else if(65==code) {
                    stcode="저혈당	";
                }else if(69==code||code==70||code==71) {
                    stcode="운동후		";
                }


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String datestr = sdf.format(cal.getTime());
                String par2[]=par[3].split("\r");

                int sugar=(Integer.parseInt(par2[0])+75);

                String now=datestr+"	"+stcode+"	"+sugar;
                if(sugar>350) {
                    System.out.println(now);
                    System.out.println("이전것	"+before+"\n");
                }

                before =now;
            }
        }catch (FileNotFoundException e) {
            e.getStackTrace();
            System.out.println(e);
        }catch(IOException e){
            e.getStackTrace();
            System.out.println(e);
        }
    }
}
