package com.pay.contextListener;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pay.model.Ip;
import com.pay.service.IpService;

/**
*@author star
*@version 创建时间：2019年6月26日上午10:09:40
*/
public class InitDataSource implements ServletContextListener {
	
	private IpService ipService;
	
	public InitDataSource() {
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext  = sce.getServletContext();
		
		WebApplicationContext c =  WebApplicationContextUtils.getWebApplicationContext(servletContext);
		ipService = c.getBean(IpService.class);
		try {
			List<Ip> ipList = ipService.getIp();
			servletContext.setAttribute("ipList", ipList); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("this is last destroyeed");  
	}

}
