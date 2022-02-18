package com.jinninghui.datasphere.icreditstudio.framework.log;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class ServerEnv implements ApplicationListener<WebServerInitializedEvent>{
	private int serverPort;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		this.serverPort = event.getWebServer().getPort();
	}

	public int getServerPort() {
		return this.serverPort;
	}
	
	public String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "0.0.0.0";
        }
	}
}
