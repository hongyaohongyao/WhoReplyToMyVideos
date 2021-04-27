package xyz.hyhy.wrtmv.utils;

public class AppContextUtils extends BaseUtils {

    public static void exit() {
        if (context != null)
            context.close();
    }

    public static Object getBean(String bean) {
        return context.getBean(bean);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }


    public static <T> T getBean(String bean, Class<T> clazz) {
        return context.getBean(bean, clazz);
    }

}
