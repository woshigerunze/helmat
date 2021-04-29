package com.acfm.ble_transform;
import com.acfm.ble_transform.SQLiteUtil.SqliteDao;

import org.json.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    private static char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;

        if ((0 <= t) &&(t <= 9)) {
            return (char)(t + '0');
        }
        return (char)(t - 10 + 'A');
    }
    //字节流转换为字符串
    public static String ByteToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        //for (int i = 0; i < bytes.length && bytes[i] != (byte) 0; i++) {
        for (int i = 0; i < bytes.length; i++) {
            //sb.append((char) (bytes[i]));
            sb.append(findHex((byte)((bytes[i] & 0xf0) >> 4)));
            sb.append(findHex((byte)(bytes[i] & 0x0f)));
        }
        return sb.toString();
    }
    //分割字符串

    public static HashMap<Character,String> binaryMap   =new HashMap<Character,String>();//静态哈希表
    public static HashMap<String,String>    gestureMap  =new HashMap<String,String>();
    public static HashMap<String,String>    modeMap     =new HashMap<String,String>();
    static {
        binaryMap.put('0',"0000");
        binaryMap.put('1',"0001");
        binaryMap.put('2',"0010");
        binaryMap.put('3',"0011");
        binaryMap.put('4',"0100");
        binaryMap.put('5',"0101");
        binaryMap.put('6',"0110");
        binaryMap.put('7',"0111");
        binaryMap.put('8',"1000");
        binaryMap.put('9',"1001");
        binaryMap.put('A',"1010");
        binaryMap.put('B',"1011");
        binaryMap.put('C',"1100");
        binaryMap.put('D',"1101");
        binaryMap.put('E',"1110");
        binaryMap.put('F',"1111");
        gestureMap.put("00","站立");
        gestureMap.put("01","静止");
        gestureMap.put("10","倒地");
        gestureMap.put("11","坠落");
        modeMap.put("00","关机");
        modeMap.put("01","开机");
        modeMap.put("10","非工作");
        modeMap.put("11","工作");
    }//对哈希表初始化，固定16进制对应的二进制字符串

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }
    public static byte[] hexStr2Byte(String hex) {
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }
    public static ArrayList<String> splitDataframe(StringBuilder str) {
        //0AFF-(00)-06A70100000000000000000078
        for(int i=0;i<str.length()-1;i++) {
            if(str.charAt(i)=='('&&str.charAt(i+1)=='-')str.setCharAt(i+1,'+');
        }
        for(int i=0;i<str.length();i++)if(str.charAt(i)==' ')str.setCharAt(i,'-');
        ArrayList<String> arrayList=new ArrayList<>();
        String regrex=str.toString();
        String[] splitArray=regrex.split("##");
        for(int i=0;i<splitArray.length;i++)arrayList.add(splitArray[i].toString());
        return arrayList;
    }//将多个数据帧拆分成单独的数据帧
    public static ArrayList<String> analysisDataframe(StringBuilder str) {
        for(int i=0;i<str.length();i++)if(str.charAt(i)==' ')str.setCharAt(i,'-');
        ArrayList<String> arrayList=new ArrayList<>();
        String regrex=str.toString();
        String[] splitArray=regrex.split("-");
        for(int i=0;i<splitArray.length;i++)arrayList.add(splitArray[i].toString());
        return arrayList;
    }//将单独的数据帧拆分
    public static ArrayList<String> dataframeToSQLite(ArrayList<String> str){
        String messageType = str.get(4).substring(1,3);
        String payLoad     = str.get(5).substring(2,24);
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(messageType);
        strings.add(payLoad);
        return strings;
    }//单独数据帧被拆分后将类型和帧内容存入SQLite
    public static String parse_Impl(String str){
        StringBuilder res=new StringBuilder();
        for(int i=0;i<str.length();i++) res.append(new StringBuilder(binaryMap.get(str.charAt(i))));
        return res.toString();
    }
    public static int getIndex(int i){
        return 7-i;}
    public static JSONObject parse(ArrayList<String> toBeparsed,SqliteDao sqliteDao) throws JSONException {

        String desId            = toBeparsed.get(3).substring(2,4);
        String messageType      =toBeparsed.get(4).substring(1,3);
        String payLoad          =toBeparsed.get(5).substring(2,24);
        JSONObject jsonObject   = new JSONObject();
        System.out.println(payLoad);

        if(messageType.equals("01")){
            String Group = payLoad.substring(0,4);
            jsonObject.put("Group",Group);
            jsonObject.put("messType","01");
        }
        else if(messageType.equals("02")){
            String Mac      = payLoad.substring(10,12)+payLoad.substring(8,10)+payLoad.substring(6,8)+payLoad.substring(4,6)+payLoad.substring(2,4)+payLoad.substring(0,2);
            String Channel  = payLoad.substring(12,14);
            String Id       = payLoad.substring(14,16);

            Integer integer1 = Integer.parseInt(Channel,2);
            Integer integer2 = Integer.parseInt(Id,2);

            jsonObject.put("Mac",Mac);
            jsonObject.put("Channel",integer1);
            jsonObject.put("Id",integer2);
            jsonObject.put("messageType","02");

            //{"Status":"未收到撤退指令未确认撤退指令正常状态工作帽子处于佩戴状态站立","Height":"0","Temperature":"26","Humidity":"0","Battery":"65%","messageType":"04"}
            //String hatId,String MAC,String temperature,String high,String power,int time
            try {

                JSONObject jsonObject1 = sqliteDao.findByHatMac(Mac);
                if("FFFFFFFFFFFF".equals(Mac)||"ffffffffffff".equals(Mac)){
                    return jsonObject;
                }
                if(jsonObject1 == null){
                    sqliteDao.insert(Id,Mac,null,null,null,System.currentTimeMillis(),null,null);

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updateByMac(Id,Mac,null,null,null,System.currentTimeMillis(),null,null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(messageType.equals("04")){
            //初步将字符串转为二进制字符串
            String SUF          =parse_Impl(payLoad.substring(0,2));//工作面数值✅
            String Status       =parse_Impl(payLoad.substring(2,4));//状态✅
            String Height       =parse_Impl(payLoad.substring(4,8));//高度✅
            String Temporary    =parse_Impl(payLoad.substring(8,10));//温度✅
            String Battery      =parse_Impl(payLoad.substring(10,12));//电池✅
            String BLE_Status   =parse_Impl(payLoad.substring(12,22));//BLE状态✅
            //对协议各字段解析
            StringBuilder SUF_parsed           =new StringBuilder();
            StringBuilder Status_parsed        =new StringBuilder();
            StringBuilder Height_parsed        =new StringBuilder();
            StringBuilder Temperature_parsed   =new StringBuilder();
            StringBuilder Humidity_parsed      =new StringBuilder();
            StringBuilder Battery_parsed       =new StringBuilder();
            StringBuilder BLE_Status_parsed    =new StringBuilder();
            //对Status解析
            if(Status.charAt(getIndex(7))=='1')
                Status_parsed.append("已收到撤退指令");
            else Status_parsed.append("未收到撤退指令");
            if(Status.charAt(getIndex(6))=='1')
                Status_parsed.append("已确认撤退指令");
            else Status_parsed.append("未确认撤退指令");
            if(Status.charAt(getIndex(5))=='1')
                Status_parsed.append("紧急呼救状态");
            else Status_parsed.append("正常状态");
            if(modeMap.get(Status.substring(3,5))!=null)
                Status_parsed.append(modeMap.get(Status.substring(3,5)));
            if(Status.charAt(getIndex(2))=='1')
                Status_parsed.append("帽子处于佩戴状态");
            else Status_parsed.append("无人佩戴状态");
            if(gestureMap.get(Status.substring(6,8))!=null)
                Status_parsed.append(gestureMap.get(Status.substring(6,8)));
            System.out.println(Status_parsed);
            //对Status解析

            //对Height解析,用Interger.parseInt求出高度
            Integer integer = Integer.parseInt(Height, 2);
            Height_parsed.append(integer);
            //对Height解析

            //对温度
            String temperature = Temporary.substring(getIndex(7),getIndex(1)+1);
            Integer integer1   = Integer.parseInt(temperature,2);
            Temperature_parsed.append(integer1);
            //对温度

            //对湿度
            String humidity  = Temporary.substring(7,8);
            humidity        +=Battery.substring(getIndex(7),getIndex(5)+1);
            Integer integer2 = Integer.parseInt(humidity,2);
            Humidity_parsed.append(integer2);
            //对湿度

            //对电池电量
            String battery   = Battery.substring(getIndex(4),getIndex(0)+1);
            Integer integer3 = Integer.parseInt(battery,2)*5;
            Battery_parsed.append(integer3).append("%");
            jsonObject.put("Status",Status_parsed);
            jsonObject.put("Height",Height_parsed);
            jsonObject.put("Temperature",Temperature_parsed);
            jsonObject.put("Humidity",Humidity_parsed);
            jsonObject.put("Battery",Battery_parsed);
            jsonObject.put("messageType","04");

            //{"Status":"未收到撤退指令未确认撤退指令正常状态工作帽子处于佩戴状态站立","Height":"0","Temperature":"26","Humidity":"0","Battery":"65%","messageType":"04"}
            //String hatId,String MAC,String temperature,String high,String power,int time
            try {
                JSONObject jsonObject1 = sqliteDao.findByHatId(desId);
                if(jsonObject1 == null){
                    //没有Mac不应该插入
                    //sqliteDao.insert(desId,null,(String)jsonObject.getString("Temperature"),(String)jsonObject.getString("Height"),(String)jsonObject.getString("Battery"),(long)System.currentTimeMillis(),jsonObject.getString("Status"),jsonObject.getString("Humidity"));

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updateByHatId(desId,(String)jsonObject.getString("Temperature"),(String)jsonObject.getString("Height"),(String)jsonObject.getString("Battery"),(long)System.currentTimeMillis(),jsonObject.getString("Status"),jsonObject.getString("Humidity"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(messageType.equals("05")){
            //初步将字符串转为二进制字符串
            String SUF      =parse_Impl(payLoad.substring(0,2));//工作面数值
            String Status   =parse_Impl(payLoad.substring(2,4));//状态
            String Height   =parse_Impl(payLoad.substring(4,8));//高度
            String Mac      =payLoad.substring(18,20)+payLoad.substring(16,18)+payLoad.substring(14,16)+payLoad.substring(12,14)+payLoad.substring(10,12)+payLoad.substring(8,10);//MAC地址
            String Version  =parse_Impl(payLoad.substring(20,22));//版本
            StringBuilder SUF_parsed           =new StringBuilder();
            StringBuilder Status_parsed        =new StringBuilder();
            StringBuilder Height_parsed        =new StringBuilder();
            StringBuilder Mac_parsed           =new StringBuilder(Mac);//直接解析
            StringBuilder Version_parsed       = new StringBuilder();
            //对Status解析
            if(Status.charAt(getIndex(7))=='1')
                Status_parsed.append("已收到撤退指令");
            else Status_parsed.append("未收到撤退指令");
            if(Status.charAt(getIndex(6))=='1')
                Status_parsed.append("已确认撤退指令");
            else Status_parsed.append("未确认撤退指令");
            if(Status.charAt(getIndex(5))=='1')
                Status_parsed.append("紧急呼救状态");
            else Status_parsed.append("正常状态");
            if(modeMap.get(Status.substring(3,5))!=null)
                Status_parsed.append(modeMap.get(Status.substring(3,5)));
            if(Status.charAt(getIndex(2))=='1')
                Status_parsed.append("帽子处于佩戴状态");
            else Status_parsed.append("无人佩戴状态");
            if(gestureMap.get(Status.substring(6,8))!=null)
                Status_parsed.append(gestureMap.get(Status.substring(6,8)));
            System.out.println(Status_parsed);
            //对Status解析

            //对Height解析,用Interger.parseInt求出高度
            Integer integer = Integer.parseInt(Height, 2);
            Height_parsed.append(integer);
            //对Height解析

            //对Version版本解析
            Integer integer1 = Integer.parseInt(Version,2);
            Version_parsed.append(integer1);
            System.out.println(integer1);
            jsonObject.put("Status",Status_parsed);
            jsonObject.put("Height",Height_parsed);
            jsonObject.put("Version",Version_parsed);
            jsonObject.put("Mac",Mac_parsed);
            jsonObject.put("messageType","05");

            //{"Status":"未收到撤退指令未确认撤退指令正常状态工作帽子处于佩戴状态站立","Height":"0","Temperature":"26","Humidity":"0","Battery":"65%","messageType":"04"}
            //String hatId,String MAC,String temperature,String high,String power,int time
            try {
                JSONObject jsonObject1 = sqliteDao.findByHatMac(Mac);
                if(jsonObject1 == null){
                    sqliteDao.insert(desId,Mac,null,(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),null);

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updateByMac(desId,Mac,null,(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(messageType.equals("06")){
            //初步将字符串转为二进制字符串
            String SUF          =parse_Impl(payLoad.substring(0,2));//工作面数值✅
            String Status       =parse_Impl(payLoad.substring(2,4));//状态✅
            String Height       =parse_Impl(payLoad.substring(4,8));//高度✅
            String Temporary    =parse_Impl(payLoad.substring(8,10));//温度✅
            String Battery      =parse_Impl(payLoad.substring(10,12));//电池✅
            String BLE_Status   =parse_Impl(payLoad.substring(12,22));//BLE状态✅
            //对协议各字段解析
            StringBuilder SUF_parsed           =new StringBuilder();
            StringBuilder Status_parsed        =new StringBuilder();
            StringBuilder Height_parsed        =new StringBuilder();
            StringBuilder Temperature_parsed   =new StringBuilder();
            StringBuilder Humidity_parsed      =new StringBuilder();
            StringBuilder Battery_parsed       =new StringBuilder();
            StringBuilder BLE_Status_parsed    =new StringBuilder();
            //对Status解析
            if(Status.charAt(getIndex(7))=='1')
                Status_parsed.append("已收到撤退指令");
            else Status_parsed.append("未收到撤退指令");
            if(Status.charAt(getIndex(6))=='1')
                Status_parsed.append("已确认撤退指令");
            else Status_parsed.append("未确认撤退指令");
            if(Status.charAt(getIndex(5))=='1')
                Status_parsed.append("紧急呼救状态");
            else Status_parsed.append("正常状态");
            if(modeMap.get(Status.substring(3,5))!=null)
                Status_parsed.append(modeMap.get(Status.substring(3,5)));
            if(Status.charAt(getIndex(2))=='1')
                Status_parsed.append("帽子处于佩戴状态");
            else Status_parsed.append("无人佩戴状态");
            if(gestureMap.get(Status.substring(6,8))!=null)
                Status_parsed.append(gestureMap.get(Status.substring(6,8)));
            System.out.println(Status_parsed);
            //对Status解析

            //对Height解析,用Interger.parseInt求出高度
            Integer integer = Integer.parseInt(Height, 2);
            Height_parsed.append(integer);
            //对Height解析

            //对温度
            String temperature = Temporary.substring(getIndex(7),getIndex(1)+1);
            Integer integer1   = Integer.parseInt(temperature,2);
            Temperature_parsed.append(integer1);
            //对温度

            //对湿度
            String humidity  = Temporary.substring(7,8);
            humidity        +=Battery.substring(getIndex(7),getIndex(5)+1);
            Integer integer2 = Integer.parseInt(humidity,2);
            Humidity_parsed.append(integer2);
            //对湿度

            //对电池电量
            String battery   = Battery.substring(getIndex(4),getIndex(0)+1);
            Integer integer3 = Integer.parseInt(battery,2)*5;
            Battery_parsed.append(integer3).append("%");
            jsonObject.put("Status",Status_parsed);
            jsonObject.put("Height",Height_parsed);
            jsonObject.put("Temperature",Temperature_parsed);
            jsonObject.put("Humidity",Humidity_parsed);
            jsonObject.put("Battery",Battery_parsed);
            jsonObject.put("messageType","06");


            try {
                JSONObject jsonObject1 = sqliteDao.findByHatId(desId);
                if(jsonObject1 == null){
                    //sqliteDao.insert(desId,null,Temperature_parsed.toString(),(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),Humidity_parsed.toString());

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updateByHatId(desId,Temperature_parsed.toString(),(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),Humidity_parsed.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(messageType.equals("07")){
            String SUF          =parse_Impl(payLoad.substring(0,2));//工作面数值✅
            String Status       =parse_Impl(payLoad.substring(2,4));//状态✅
            String Height       =parse_Impl(payLoad.substring(4,8));//高度✅
            String Mac          =payLoad.substring(18,20)+payLoad.substring(16,18)+payLoad.substring(14,16)+payLoad.substring(12,14)+payLoad.substring(10,12)+payLoad.substring(8,10);//MAC地址
            StringBuilder SUF_parsed           =new StringBuilder();
            StringBuilder Status_parsed        =new StringBuilder();
            StringBuilder Height_parsed        =new StringBuilder();
            StringBuilder Mac_parsed           =new StringBuilder(Mac);//直接解析
            //对Status解析
            if(Status.charAt(getIndex(7))=='1')
                Status_parsed.append("已收到撤退指令");
            else Status_parsed.append("未收到撤退指令");
            if(Status.charAt(getIndex(6))=='1')
                Status_parsed.append("已确认撤退指令");
            else Status_parsed.append("未确认撤退指令");
            if(Status.charAt(getIndex(5))=='1')
                Status_parsed.append("紧急呼救状态");
            else Status_parsed.append("正常状态");
            if(modeMap.get(Status.substring(3,5))!=null)
                Status_parsed.append(modeMap.get(Status.substring(3,5)));
            if(Status.charAt(getIndex(2))=='1')
                Status_parsed.append("帽子处于佩戴状态");
            else Status_parsed.append("无人佩戴状态");
            if(gestureMap.get(Status.substring(6,8))!=null)
                Status_parsed.append(gestureMap.get(Status.substring(6,8)));
            System.out.println(Status_parsed);
            //对Status解析

            //对Height解析,用Interger.parseInt求出高度
            Integer integer = Integer.parseInt(Height, 2);
            Height_parsed.append(integer);
            //对Height解析

            jsonObject.put("Status",Status_parsed);
            jsonObject.put("Height",Height_parsed);
            jsonObject.put("Mac",Mac_parsed);
            jsonObject.put("messageType","07");

            try {
                JSONObject jsonObject1 = sqliteDao.findByHatMac(Mac);
                if(jsonObject1 == null){
                    sqliteDao.insert(null,Mac,null,(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),null);

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updateByMac(null,Mac,null,(String)jsonObject.getString("Height"),null,(long)System.currentTimeMillis(),jsonObject.getString("Status"),null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(messageType.equals("08")){
            String Height       = parse_Impl(payLoad.substring(0,4));
            String Temperature  = parse_Impl(payLoad.substring(4,6));
            String Battery      = parse_Impl(payLoad.substring(6,8));
            String Worktime     = parse_Impl(payLoad.substring(8,16));

            StringBuilder Height_parsed         = new StringBuilder();
            StringBuilder Temperature_parsed    = new StringBuilder();
            StringBuilder Battery_parsed        = new StringBuilder();
            StringBuilder Worktime_parsed       =new StringBuilder();

            Integer integer1 = Integer.parseInt(Height,2);
            Integer integer2 = Integer.parseInt(Temperature,2);
            Integer integer3 = Integer.parseInt(Battery,2)*5;
            Integer integer4 = Integer.parseInt(Worktime,2);

            Height_parsed.append(integer1);
            Temperature_parsed.append(integer2);
            Battery_parsed.append(integer3);
            Worktime_parsed.append(integer4).append("s");

            jsonObject.put("Height",Height_parsed);
            jsonObject.put("Temperature",Temperature_parsed);
            jsonObject.put("Battery",Battery_parsed);
            jsonObject.put("Worktime",Worktime_parsed);
            jsonObject.put("messageType","08");

            try {
                JSONObject jsonObject1 = sqliteDao.findbyrepeaterid(desId);
                if(jsonObject1 == null){
                    sqliteDao.insertrepeater(desId,Temperature_parsed.toString(),Height_parsed.toString(),Worktime_parsed.toString(),(long)System.currentTimeMillis());

                }else{
                    //public void updateById(String hatId,String temperature,String high,String power,long time,String status,String humidity){
                    sqliteDao.updatebyrepeaterid(desId,Temperature_parsed.toString(),Height_parsed.toString(),Worktime_parsed.toString(),(long)System.currentTimeMillis());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

}
