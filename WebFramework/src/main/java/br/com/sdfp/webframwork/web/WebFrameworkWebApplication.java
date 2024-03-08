package br.com.sdfp.webframwork.web;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import br.com.sdfp.webframwork.annotations.WebframeworkDeleteMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkGetMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkPostMethod;
import br.com.sdfp.webframwork.annotations.WebframeworkPutMethod;
import br.com.sdfp.webframwork.datastructures.ControllerMap;
import br.com.sdfp.webframwork.datastructures.MethodParam;
import br.com.sdfp.webframwork.datastructures.RequestControllerData;
import br.com.sdfp.webframwork.datastructures.ServiceImplementationMap;
import br.com.sdfp.webframwork.explorer.ClassExplorer;
import br.com.sdfp.webframwork.util.WebFrameworkLogger;
import br.com.sdfp.webframwork.util.WebFrameworkUtil;

public class WebFrameworkWebApplication {

	public static void run(Class<?> sourceClass) {
		//desligar os log do apache tomcat
		java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
		long ini, fim;
		
		WebFrameworkLogger.showBanner();
		
		try {
			//class explorer
			//Comecar a criar um metodo de extração de metadado:
			/*List<String> allClasses = ClassExplorer.retrieveAllClasses(sourceClass);
			allClasses.forEach( p ->{
				WebFrameworkLogger.log("Class Explorer", "Class found: " + p);
			});*/
			extractMetadata(sourceClass);
			ini = System.currentTimeMillis();
			WebFrameworkLogger.log("Embeded web Conteiner", "Iniciando WebframworkWebApplication");
			Tomcat tomcat = new Tomcat();
			Connector connector = new Connector();
			connector.setPort(8080);
			tomcat.setConnector(connector);
			WebFrameworkLogger.log("Embeded web Conteiner", "Iniciando na porta 8080");
			
			//contexto olhando para raiz de aplicação
			Context context = tomcat.addContext("", new File(".").getAbsolutePath());
			Tomcat.addServlet(context, "WebframeWorkDispatcherServlet", new WebframeWorkDispatcherServlet());
		// tudo que digitar na URL vai cair neste ponto: localhost:8080/*
			context.addServletMappingDecoded("/*", "WebframeWorkDispatcherServlet");
			
			fim = System.currentTimeMillis();
			WebFrameworkLogger.log("Embeded web Conteiner", "Tomcat iniciado em "
					+(double) ((fim - ini)/1000) +"s");
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void extractMetadata(Class<?> sourceClass) {
		try {
			List<String> allClasses = ClassExplorer.retrieveAllClasses(sourceClass);
			for(String classe : allClasses) {
				//recuperar as anotaçoes da classe
				Annotation annotations[] = Class.forName(classe).getAnnotations();
				for (Annotation classAnotations : annotations) {
					if (classAnotations.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebFrameworkController")) {
						WebFrameworkLogger.log("Meradata Explorer", "Found a controller" + classe);
						extractMethods(classe);
					}else if(classAnotations.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkService")) {
						WebFrameworkLogger.log("Metadata Explorer", "Found a Service Implementation: " + classe);
						for(Class<?> interfaceWeb : Class.forName(classe).getInterfaces()) {
							WebFrameworkLogger.log("Metadata Explorer", "     Class implements" + interfaceWeb.getName());
							ServiceImplementationMap.implementations.put(interfaceWeb.getName(), classe);
						}
					}
					
				}
			}
			for(RequestControllerData item : ControllerMap.values.values()) {
				WebFrameworkLogger.log("", "     " + item.getHttpMethod() + ":" + item.getUrl() +
											" [" + item.getControllerClass() + "." + item.getControllerMethod() + "]"+ (item.getParameter().length() > 0 ? " - Expected parameter " + item.getParameter() : "")
						);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private static void extractMethods(String className) throws Exception{
		String httpMethod = "";
		String path = "";
		String parameter = "";
		
		//recuperar todos os metodos da classe
		for(Method method : Class.forName(className).getDeclaredMethods()) {
			//WebFrameworkLogger.log(" - ", method.getName());
			for(Annotation annotation : method.getAnnotations()) {
				if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkGetMethod")) {
					httpMethod = "GET";
					path = ((WebframeworkGetMethod)annotation).value();
					
					//verificar se existe parâmetro no path.
					MethodParam methodParam = WebFrameworkUtil.convertURI2MethodParam(path);
					if(methodParam != null) {
						path = methodParam.getMethod();
						if(methodParam.getParam() != null)
							parameter = methodParam.getParam(); 
					}
					
				}else if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkPostMethod")) {
					httpMethod = "POST";
					path = ((WebframeworkPostMethod)annotation).value();
					
					//verificar se existe parâmetro no path.
					MethodParam methodParam = WebFrameworkUtil.convertURI2MethodParam(path);
					if(methodParam != null) {
						path = methodParam.getMethod();
						if(methodParam.getParam() != null)
							parameter = methodParam.getParam(); 
					}
				}else if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkPutMethod")) {
					httpMethod = "PUT";
					path = ((WebframeworkPutMethod)annotation).value();
					//verificar se existe parâmetro no path.
					MethodParam methodParam = WebFrameworkUtil.convertURI2MethodParam(path);
					if(methodParam != null) {
						path = methodParam.getMethod();
						if(methodParam.getParam() != null)
							parameter = methodParam.getParam(); 
					}
				}else if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkDeleteMethod")) {
					httpMethod = "DELETE";
					path = ((WebframeworkDeleteMethod)annotation).value();
					//verificar se existe parâmetro no path.
					MethodParam methodParam = WebFrameworkUtil.convertURI2MethodParam(path);
					if(methodParam != null) {
						path = methodParam.getMethod();
						if(methodParam.getParam() != null)
							parameter = methodParam.getParam(); 
					}
				}
				WebFrameworkLogger.log(" - CHAVE: ", httpMethod  + path);
			}
			RequestControllerData getData = new RequestControllerData(httpMethod, path, className, method.getName(), parameter);
			ControllerMap.values.put(httpMethod + path, getData);
		}
	}
	
}
