package xyz.hyhy.wrtmv.utils;

import xyz.hyhy.wrtmv.config.CommonConfig;
import xyz.hyhy.wrtmv.utils.dotask.RunAndWait;
import xyz.hyhy.wrtmv.utils.dotask.RunAndWaitImpl;

import javax.annotation.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;


/**
 * 一般的工具类
 */
public class CommonUtils {
    @Resource
    private static CommonConfig commonConfig;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private CommonUtils() {

    }

    public static void closeAll(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null)
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static RunAndWait getRunAndWaitInstance() {
        return new RunAndWaitImpl();
    }

    public static RunAndWait getRunAndWaitInstance(ExecutorService pool) {
        return new RunAndWaitImpl(pool);
    }

    public static long getToDay() {
        try {
            return simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().getTime();
    }
}
