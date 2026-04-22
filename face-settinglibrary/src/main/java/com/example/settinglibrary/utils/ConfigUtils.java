package com.example.settinglibrary.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.example.settinglibrary.R;
import com.example.datalibrary.threshold.SingleBaseConfig;
import com.example.datalibrary.utils.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * author : shangrong
 * date : 2019/5/23 11:46 AM
 * description :对配置文件进行读取和修改
 */
public class ConfigUtils {

    public static String folder;

    /*配置文件路径*/
    public static String filePath;


    public static boolean isConfigExit(Context context) {
        folder = context.getFilesDir() + File.separator + "Settings";
        filePath =  folder + "/" + "gateFaceConfig.txt";
        File file1 = new File(folder);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            try {
                file.createNewFile();
                modityJson(context);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }


    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 读取配置文件内容
     *
     * @return
     */
    public static Boolean initConfig(Context context) {
        String configMessage = FileUtils.txt2String(filePath);

        if (configMessage == null || configMessage.length() == 0) {
            Log.e("facesdk", context.getResources().getString(R.string.log_file_not_exist));
            return false;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(configMessage);
            /*if (!identify(jsonObject)) {
                return false;
            }*/
            SingleBaseConfig.getBaseConfig().setMinimumFace(jsonObject.getInt("minimumFace"));
            SingleBaseConfig.getBaseConfig().setBlur(Float.valueOf(jsonObject.get("blur") + ""));
            SingleBaseConfig.getBaseConfig().setIllumination(Float.valueOf(jsonObject.get("illum") + ""));
            SingleBaseConfig.getBaseConfig().setGesture(Integer.valueOf(jsonObject.get("gesture") + ""));
            SingleBaseConfig.getBaseConfig().setLeftEye(Float.valueOf(jsonObject.get("leftEye") + ""));
            SingleBaseConfig.getBaseConfig().setRightEye(Float.valueOf(jsonObject.get("rightEye") + ""));
            SingleBaseConfig.getBaseConfig().setNose(Float.valueOf(jsonObject.get("nose") + ""));
            SingleBaseConfig.getBaseConfig().setMouth(Float.valueOf(jsonObject.get("mouth") + ""));
            SingleBaseConfig.getBaseConfig().setLeftCheek(Float.valueOf(jsonObject.get("leftCheek") + ""));
            SingleBaseConfig.getBaseConfig().setRightCheek(Float.valueOf(jsonObject.get("rightCheek") + ""));
            SingleBaseConfig.getBaseConfig().setChinContour(Float.valueOf(jsonObject.get("chinContour") + ""));
//            SingleBaseConfig.getBaseConfig().setThreshold(jsonObject.getInt("threshold"));
            SingleBaseConfig.getBaseConfig().
                    setLiveThreshold(Float.valueOf(jsonObject.getString("liveScoreThreshold")));
            SingleBaseConfig.getBaseConfig().setIdThreshold(Float.valueOf(jsonObject.getString("idScoreThreshold")));
            SingleBaseConfig.getBaseConfig().setRgbAndNirThreshold
                    (Float.valueOf(jsonObject.getString("rgbAndNirScoreThreshold")));
            SingleBaseConfig.getBaseConfig().setCameraLightThreshold(jsonObject.getInt("cameraLightThreshold"));
            SingleBaseConfig.getBaseConfig().setActiveModel(jsonObject.getInt("activeModel"));
            SingleBaseConfig.getBaseConfig().setType(jsonObject.getInt("type"));
            SingleBaseConfig.getBaseConfig().setQualityControl(jsonObject.getBoolean("qualityControl"));
            SingleBaseConfig.getBaseConfig().setLivingControl(jsonObject.getBoolean("livingControl"));
            SingleBaseConfig.getBaseConfig().setRgbLiveScore(Float.valueOf(jsonObject.get("rgbLiveScore") + ""));
            SingleBaseConfig.getBaseConfig().setNirLiveScore(Float.valueOf(jsonObject.get("nirLiveScore") + ""));
            SingleBaseConfig.getBaseConfig().setDepthLiveScore(Float.valueOf(jsonObject.get("depthLiveScore") + ""));
            SingleBaseConfig.getBaseConfig().setCameraType(jsonObject.getInt("cameraType"));
            SingleBaseConfig.getBaseConfig().setRgbRevert(jsonObject.getBoolean("RGBRevert"));
            SingleBaseConfig.getBaseConfig().setAttribute(jsonObject.getBoolean("attribute"));
            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(jsonObject.getInt("rgbAndNirWidth"));
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(jsonObject.getInt("rgbAndNirHeight"));
            SingleBaseConfig.getBaseConfig().setDepthWidth(jsonObject.getInt("depthWidth"));
            SingleBaseConfig.getBaseConfig().setDepthHeight(jsonObject.getInt("depthHeight"));
            SingleBaseConfig.getBaseConfig().setFaceThreshold(Float.valueOf(jsonObject.get("faceThreshold") + ""));
            SingleBaseConfig.getBaseConfig().setDarkEnhance(jsonObject.getBoolean("darkEnhance"));
            SingleBaseConfig.getBaseConfig().setBestImage(jsonObject.getBoolean("bestImage"));
            SingleBaseConfig.getBaseConfig().setLog(jsonObject.getBoolean("log"));
            SingleBaseConfig.getBaseConfig().setRgbVideoDirection(jsonObject.getInt("rgbVideoDirection"));
            SingleBaseConfig.getBaseConfig().setMirrorVideoRGB(jsonObject.getInt("mirrorVideoRGB"));
            SingleBaseConfig.getBaseConfig().setNirVideoDirection(jsonObject.getInt("nirVideoDirection"));
            SingleBaseConfig.getBaseConfig().setMirrorVideoNIR(jsonObject.getInt("mirrorVideoNIR"));
            SingleBaseConfig.getBaseConfig().setRgbDetectDirection(jsonObject.getInt("rgbDetectDirection"));
            SingleBaseConfig.getBaseConfig().setMirrorDetectRGB(jsonObject.getInt("mirrorDetectRGB"));
            SingleBaseConfig.getBaseConfig().setNirDetectDirection(jsonObject.getInt("nirDetectDirection"));
            SingleBaseConfig.getBaseConfig().setMirrorDetectNIR(jsonObject.getInt("mirrorDetectNIR"));
            SingleBaseConfig.getBaseConfig().setRBGCameraId(jsonObject.getInt("rbgCameraId"));
            SingleBaseConfig.getBaseConfig().setOpenGl(jsonObject.getBoolean("isOpenGl"));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("facesdk", context.getResources().getString(R.string.log_file_content_abnormal) + " " + e.getMessage());
            return false;
        }
    }

    /**
     * 修改配置文件内容并重新读取配置
     */
    public static boolean modityJson(Context context) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("minimumFace", SingleBaseConfig.getBaseConfig().getMinimumFace());
            jsonObject.put("blur", String.valueOf(SingleBaseConfig.getBaseConfig().getBlur()));
            jsonObject.put("illum", SingleBaseConfig.getBaseConfig().getIllumination());
            jsonObject.put("gesture", SingleBaseConfig.getBaseConfig().getGesture());
            jsonObject.put("leftEye", String.valueOf(SingleBaseConfig.getBaseConfig().getLeftEye()));
            jsonObject.put("rightEye", String.valueOf(SingleBaseConfig.getBaseConfig().getRightEye()));
            jsonObject.put("nose", String.valueOf(SingleBaseConfig.getBaseConfig().getNose()));
            jsonObject.put("mouth", String.valueOf(SingleBaseConfig.getBaseConfig().getMouth()));
            jsonObject.put("leftCheek", String.valueOf(SingleBaseConfig.getBaseConfig().getLeftCheek()));
            jsonObject.put("rightCheek", String.valueOf(SingleBaseConfig.getBaseConfig().getRightCheek()));
            jsonObject.put("chinContour", String.valueOf(SingleBaseConfig.getBaseConfig().getChinContour()));
            jsonObject.put("liveScoreThreshold", SingleBaseConfig.getBaseConfig().getLiveThreshold());
            jsonObject.put("idScoreThreshold", SingleBaseConfig.getBaseConfig().getIdThreshold());
            jsonObject.put("rgbAndNirScoreThreshold", SingleBaseConfig.getBaseConfig().getRgbAndNirThreshold());
            jsonObject.put("cameraLightThreshold", SingleBaseConfig.getBaseConfig().getCameraLightThreshold());
            jsonObject.put("activeModel", SingleBaseConfig.getBaseConfig().getActiveModel());
            jsonObject.put("type", SingleBaseConfig.getBaseConfig().getType());
            jsonObject.put("qualityControl", SingleBaseConfig.getBaseConfig().isQualityControl());
            jsonObject.put("livingControl", SingleBaseConfig.getBaseConfig().isLivingControl());
            jsonObject.put("rgbLiveScore", SingleBaseConfig.getBaseConfig().getRgbLiveScore());
            jsonObject.put("nirLiveScore", SingleBaseConfig.getBaseConfig().getNirLiveScore());
            jsonObject.put("depthLiveScore", SingleBaseConfig.getBaseConfig().getDepthLiveScore());
            jsonObject.put("cameraType", SingleBaseConfig.getBaseConfig().getCameraType());
            jsonObject.put("RGBRevert", SingleBaseConfig.getBaseConfig().getRgbRevert());
            jsonObject.put("attribute", SingleBaseConfig.getBaseConfig().isAttribute());
            jsonObject.put("rgbAndNirWidth", SingleBaseConfig.getBaseConfig().getRgbAndNirWidth());
            jsonObject.put("rgbAndNirHeight", SingleBaseConfig.getBaseConfig().getRgbAndNirHeight());
            jsonObject.put("depthWidth", SingleBaseConfig.getBaseConfig().getDepthWidth());
            jsonObject.put("depthHeight", SingleBaseConfig.getBaseConfig().getDepthHeight());
            jsonObject.put("faceThreshold", SingleBaseConfig.getBaseConfig().getFaceThreshold());
            jsonObject.put("darkEnhance", SingleBaseConfig.getBaseConfig().isDarkEnhance());
            jsonObject.put("bestImage", SingleBaseConfig.getBaseConfig().isBestImage());
            jsonObject.put("log", SingleBaseConfig.getBaseConfig().isLog());
            jsonObject.put("rgbVideoDirection", SingleBaseConfig.getBaseConfig().getRgbVideoDirection());
            jsonObject.put("mirrorVideoRGB", SingleBaseConfig.getBaseConfig().getMirrorVideoRGB());
            jsonObject.put("nirVideoDirection", SingleBaseConfig.getBaseConfig().getNirVideoDirection());
            jsonObject.put("mirrorVideoNIR", SingleBaseConfig.getBaseConfig().getMirrorVideoNIR());
            jsonObject.put("rgbDetectDirection", SingleBaseConfig.getBaseConfig().getRgbDetectDirection());
            jsonObject.put("mirrorDetectRGB", SingleBaseConfig.getBaseConfig().getMirrorDetectRGB());
            jsonObject.put("nirDetectDirection", SingleBaseConfig.getBaseConfig().getNirDetectDirection());
            jsonObject.put("mirrorDetectNIR", SingleBaseConfig.getBaseConfig().getMirrorDetectNIR());
            jsonObject.put("rbgCameraId", SingleBaseConfig.getBaseConfig().getRBGCameraId());
            jsonObject.put("isOpenGl", SingleBaseConfig.getBaseConfig().isOpenGl());
            // 修改内容写入配置文件
            FileUtils.writeTxtFile(jsonObject.toString(), filePath);
            // 重新读取配置文件内容
            initConfig(context);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }

    /**
     * 判断数字正则表达式
     *
     * @param str
     * @return
     */
    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;

    }

    /**
     * 判断字符正则表达式
     *
     * @param str
     * @return
     */
    public boolean isString(String str) {

        return str.matches("[a-zA-Z] + ");

    }


   
    public static <T> T gotObjectByObject(Context context , Object object, Class<T> clazz) throws Exception {
        T t = null;
        if (clazz != null && object != null) {
            t = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.getName();
                try {
                    Field field1 = object.getClass().getDeclaredField(key);
                    field1.setAccessible(true);
                    Object val = field1.get(object);
                    field.set(t, val);
                } catch (Exception e) {
                    t = null;
                    System.out.println(object.getClass().getName() + context.getResources().getString(R.string.reflection_no_property) + key);
                }
            }
        }
        return t;
    }


}
