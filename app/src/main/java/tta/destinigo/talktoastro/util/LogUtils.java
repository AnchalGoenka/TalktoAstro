package tta.destinigo.talktoastro.util;

import tta.destinigo.talktoastro.util.logger.Logger;
import tta.destinigo.talktoastro.util.logger.SystemUtil;
import tta.destinigo.talktoastro.util.logger.Logger;

/**
 * Created by Vivek Singh on 2019-06-10.
 */
public final class LogUtils {

    public static final String COPY_SUCCESS = "Copied to clipboard.";
    public static final String COPY = "Copy";
    public static final String CANCEL = "Cancel";

    private static Logger logger;
    public static boolean ENABLE_LOG;

    static {
        logger = new Logger();
    }

    /**
     *
     */
    //public static boolean DEBUG = true;

    public static String configTagPrefix = "";

    /**
     * @param msg
     * @param args
     */
    public static void v(String msg, Object... args) {
        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            v(ms);
        }
        else
            v(args[0].toString());
        //(SystemUtil.getStackTrace(), msg, args);
    }

    private static void v(String object) {
        logger.v(SystemUtil.getStackTrace(), object);
    }


    /**
     * @param msg
     * @param args
     */
    public static void d(String msg, Object... args) {
        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            d(ms);
        }
        else
            d(args[0].toString());
    }

    public static void d(String object) {
        logger.d(SystemUtil.getStackTrace(), object);
    }

    /**
     * @param msg
     * @param args
     */
    public static void i(String msg, Object... args) {
        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            i(ms);
        }
        else
            i(args[0].toString());
    }

    public static void i(String object) {
        logger.i(SystemUtil.getStackTrace(), object);
    }

    /**
     * @param msg
     * @param args
     */
    public static void w(String msg, Object... args) {
        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            w(ms);
        }
        else
            w(args[0].toString());
    }

    public static void w(String object) {
        logger.w(SystemUtil.getStackTrace(), object);
    }

    /**
     * @param msg
     * @param args
     */
    public static void e(String msg, Object... args) {
        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            e(ms);
        }
        else{}
         //   e(args[0].toString());
    }

    public static void e(String object) {
        logger.e(SystemUtil.getStackTrace(), object);
    }

    /**
     * @param msg
     * @param args
     */
    public static void wtf(String msg, Object... args) {

        if(args.length>1 && args instanceof String[]){
            String ms="";
            for(Object s : args)
                ms+="||"+ s;
            wtf(ms);
        }
        else
            wtf(args[0]);
    }

    private static void wtf(Object object) {
        logger.wtf(SystemUtil.getStackTrace(), object);
    }

    /**
     * @param json
     */
    public static void json(String json) {
        logger.json(SystemUtil.getStackTrace(), json);
    }

}
