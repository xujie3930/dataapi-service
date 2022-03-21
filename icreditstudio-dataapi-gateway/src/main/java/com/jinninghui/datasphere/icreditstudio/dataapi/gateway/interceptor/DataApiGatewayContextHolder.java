package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor
 * ClassName: ThreadLocalContextHolder
 * Description:  ThreadLocalContextHolder类
 * Date: 2022/3/17 2:47 下午
 *
 * @author liyanhui
 */
public final class DataApiGatewayContextHolder {

    private DataApiGatewayContextHolder(){}

    private static final ThreadLocal<DataApiGatewayContext> context = new ThreadLocal<>();

    public static void set(DataApiGatewayContext dataApiGatewayContext){
        context.set(dataApiGatewayContext);
    }

    public static DataApiGatewayContext get(){
        return context.get();
    }

    public static void remove(){
        context.remove();
    }

}
