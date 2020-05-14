package com.example.androidlatteroom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.sql.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeviceSettingService extends Service {
    public static String device = "Android1";
    //private static Device device = new Device();

    public DeviceSettingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    public static LatteMessage makeSensorMsg(SensorData data){
        return new LatteMessage(data);
    }

//    public static LatteMessage makeRequestMsg(String sensorId){
//        return new LatteMessage(sensorId);
//    }

    public static LatteMessage makeAlertMsg(Alert data){
        return new LatteMessage(data);
    }

    public static Sensor makeSensor(String sensorType){
        return new Sensor(sensorType);
    }

    public static SensorData makeSensorData(String sensorID, String states){
        return new SensorData(sensorID,states);
    }

    public static SensorData makeSensorData(String sensorID, String states, String stateDetail){
        return new SensorData(sensorID,states,stateDetail);
    }


}
class Alert {
    private String deviceID;
    private int hour;           // 시간
    private int min;            // 분
    private String weeks;       // 알람 수행 요일
    private boolean flag;       // 알람 on/off


    // constructor
    private Alert() {
        this.deviceID = "Android01";
    }

    public Alert(int hour, int min, String weeks, boolean flag) {
        this();
        this.hour = hour;
        this.min = min;
        this.weeks = weeks;
        this.flag = flag;
    }


    // get, set
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}

class LatteMessage {
    private String deviceID;
    private String voType;
    private String jsonData;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();


    // constructor
    private LatteMessage() {
        this.deviceID = "Android01";
    }

    public LatteMessage(SensorData data) {
        this();
        this.voType = "SensorData";
        this.jsonData = LatteMessage.gson.toJson(data);
    }

    public LatteMessage(Alert data) {
        this();
        this.voType = "Alert";
        this.jsonData = LatteMessage.gson.toJson(data);
    }

    public LatteMessage(String states, String stateDetail) {
        this();
        this.voType = "Request";
        this.jsonData = LatteMessage.gson.toJson(new SensorData(states, stateDetail));
    }
    public LatteMessage(String SensorId) {
        this();
        this.voType = "Request";
        this.jsonData = SensorId;
    }

    // get, set method
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getVoType() {
        return voType;
    }

    public void setVoType(String voType) {
        this.voType = voType;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public String toString() {
        return "Message [deviceID=" + deviceID + ", voType=" + voType + ", jsonData=" + jsonData + "]";
    }

}
class Sensor {

    private String deviceID = "Android01";
    private String sensorID = "" + this.hashCode();
    private String sensorType;
    private SensorData recentData;


    // constructor
    public Sensor(String sensorType) {
        this.sensorType = sensorType;
    }
//
//    public Sensor(LatteBaseClient device, String sensorType) {
//        this.deviceID = LatteBaseClient.getDeviceId();
//        this.sensorType = sensorType;
//    }


    // custom method
    public String getStates() {
        return this.recentData.getStates();
    }

    public String getStateDetail() {
        return this.recentData.getStateDetail();
    }

    // 지정된 센서에 최신 데이터 업데이트 (states)
    public SensorData setRecentData(String states) {
        this.recentData = new SensorData(this.sensorID, states);
        return this.recentData;
    }

    // 지정된 센서에 최신 데이터 업데이트 (states, stateDetail)
    public SensorData setRecentData(String states, String stateDetail) {
        this.recentData = new SensorData(this.sensorID, states, stateDetail);
        return this.recentData;
    }


    // get, set
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public SensorData getRecentData() {
        return recentData;
    }

//	public void setRecentData(SensorData recentData) {
//		this.recentData = recentData;
//	}

    public SensorData setRecentData(SensorData data) {
        this.recentData = data;
        return this.recentData;
    }

}
class SensorData {
    private int dataID;
    private String sensorID;
    private Date time;
    private String states;
    private String stateDetail;


    // constructor
    private SensorData() {
        this.dataID = this.hashCode();
        this.time = new Date(System.currentTimeMillis());
    }

    public SensorData(String sensorID, String states) {
        this();
        this.sensorID = sensorID;
        this.states = states;
    }

    public SensorData(String sensorID, String states, String stateDetail) {
        this(sensorID, states);
        this.stateDetail = stateDetail;
    }


    // get, set
    public int getDataID() {
        return dataID;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getStateDetail() {
        return stateDetail;
    }

    public void setStateDetail(String stateDetail) {
        this.stateDetail = stateDetail;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "dataID=" + dataID +
                ", sensorID='" + sensorID + '\'' +
                ", time=" + time +
                ", states='" + states + '\'' +
                ", stateDetail='" + stateDetail + '\'' +
                '}';
    }
}


