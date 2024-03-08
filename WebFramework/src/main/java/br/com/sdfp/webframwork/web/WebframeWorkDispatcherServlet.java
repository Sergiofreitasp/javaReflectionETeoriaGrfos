package br.com.sdfp.webframwork.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.google.gson.Gson;

import br.com.sdfp.webframwork.annotations.WebframeworkBody;
import br.com.sdfp.webframwork.datastructures.ControllerInstances;
import br.com.sdfp.webframwork.datastructures.ControllerMap;
import br.com.sdfp.webframwork.datastructures.RequestControllerData;
import br.com.sdfp.webframwork.datastructures.ServiceImplementationMap;
import br.com.sdfp.webframwork.datastructures.DependencyInjectionMap;
import br.com.sdfp.webframwork.datastructures.MethodParam;
import br.com.sdfp.webframwork.util.WebFrameworkLogger;
import br.com.sdfp.webframwork.util.WebFrameworkUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebframeWorkDispatcherServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//super.service(req, res);
		
		//ingonorar favIcon
		if (req.getRequestURL().toString().endsWith("/favicon.ico")) {
			return;
		}
		PrintWriter out = new PrintWriter(resp.getWriter());
		Gson gson =new Gson();
		
		//String url = req.getRequestURI();
		
		MethodParam methodParam = WebFrameworkUtil.convertURI2MethodParam(req.getRequestURI());
		if(methodParam == null)
			return;
		String url = methodParam.getMethod();
		
		String httpMethod = req.getMethod().toUpperCase();
		
		String key = httpMethod + url;
		
		RequestControllerData data = ControllerMap.values.get(key);//buscar a classe pela chave
		
		WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "URL: " + url + "(" + httpMethod + ") - Handler " + data.getControllerClass() + "." + data.getControllerMethod());
		
		//verificar se existe uma instancia da classe correspondente, caso não, criar uma
		Object controller;
		//WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Procurar Instância da Controladora");
		try {
			controller = ControllerInstances.instace.get(data.controllerClass);
			if(controller == null) {
				WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Criar nova Instância da Controladora");
				controller = Class.forName(data.controllerClass).getDeclaredConstructor().newInstance();
				ControllerInstances.instace.put(data.controllerClass, controller);
				
				injectDependencies(controller);
			}
			Method controllerMethod = null;
			for(Method method : controller.getClass().getMethods()) {
				if(method.getName().equals(data.controllerMethod)) {
					controllerMethod = method;
					break;
				}
			}
			
			//meu metodo tem parametros??
			if (controllerMethod.getParameterCount() > 0) {
				WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Método " + controllerMethod.getName() + " tem parâmetros!");
				/*Object arg;
				Parameter parameter = controllerMethod.getParameters()[0];
				if (parameter.getAnnotations()[0].annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkBody")) { //
					WebFrameworkLogger.log("", "     Procurando parâmetro da requisição do tipo " + parameter.getType().getName());
					String body = readBytesFromRequest(req);
					
					WebFrameworkLogger.log("", "     conteúdo do parâmetro: " + body);
					arg = gson.fromJson(body, parameter.getType());
					
					WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Invocar o método " + controllerMethod.getName() +" com o parâmetro do tipo " + parameter.getType().toString() + " para requisição");				
					out.println(gson.toJson(controllerMethod.invoke(controller, arg)));
				} */
				Object arg = null;
				Object arg2 = null;
				Parameter[] parameters = controllerMethod.getParameters();
				for(Parameter parameter : parameters) {
					for(Annotation annotation : parameter.getAnnotations()) {
						if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkBody")) {
							WebFrameworkLogger.log("", "     Procurando parâmetro da requisição do tipo " + parameter.getType().getName());
							String body = readBytesFromRequest(req); //pegando todo o corpo da requisicao
							
							WebFrameworkLogger.log("", "     conteúdo do parâmetro: " + body);
							if (parameters.length > 1) {
								arg2 = gson.fromJson(body, parameter.getType());
							}else {
								arg = gson.fromJson(body, parameter.getType());
							}
							
							WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Invocar o método " + controllerMethod.getName() +" com o parâmetro do tipo " + parameter.getType().toString() + " para requisição");				
							
						}else if(annotation.annotationType().getName().equals("br.com.sdfp.webframwork.annotations.WebframeworkPathVariable")) {
							WebFrameworkLogger.log("", "     Procurando parâmetro da requisição do tipo " 
									+ parameter.getType().getName());
							WebFrameworkLogger.log("", "     Conteúdo do parâmetro: " 
									+ methodParam.getParam());
							
							arg = WebFrameworkUtil.convert2Type(methodParam.getParam(), parameter.getType());
							
						}
					}
				}
				if (parameters.length > 1) {
					out.println(gson.toJson(controllerMethod.invoke(controller, arg, arg2)));
				}else {
					out.println(gson.toJson(controllerMethod.invoke(controller, arg)));
				}
			} else {
				WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Invocar o método " + controllerMethod.getName() + " para requisição");				
				out.println(gson.toJson(controllerMethod.invoke(controller)));
			}
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void injectDependencies(Object controller) throws Exception {
		//ver apenas os campos anotados por Inject
		for(Field attr : controller.getClass().getDeclaredFields()) {
			String attrTipo = attr.getType().getName();
			WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Injetar " + attr.getName() + " do tipo " + attrTipo);
			Object serviceImpl;
			if(DependencyInjectionMap.objects.get(attrTipo)== null) {
				//tem declaração da interface?
				String implType = ServiceImplementationMap.implementations.get(attrTipo);
				if(implType != null) {
					WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Procurar Instâncias de " + implType);
					serviceImpl = DependencyInjectionMap.objects.get(implType);
					if(serviceImpl == null) {
						WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Injetar novo objeto");
						serviceImpl = Class.forName(implType).getDeclaredConstructor()
								.newInstance();
						DependencyInjectionMap.objects.put(implType, serviceImpl);
					}
					//atribuir essa instancia ao atributo anotado - Injeção de dependência.
					attr.setAccessible(true);
					attr.set(controller, serviceImpl);
					WebFrameworkLogger.log("WebFrameworkDispatcherServlet", "Objeto injetado com sucesso!");
				}
			}
			
		}
	}
	
	private String readBytesFromRequest(HttpServletRequest req) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		while((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		return stringBuilder.toString();
	}
	
}
