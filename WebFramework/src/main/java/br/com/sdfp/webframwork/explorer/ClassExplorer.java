package br.com.sdfp.webframwork.explorer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ClassExplorer {

	public static List<String> retrieveAllClasses(Class<?> sourceClass){
		return packagExplorer(sourceClass.getPackageName());
	}
	
	private static List<String> packagExplorer(String packageName){
		ArrayList<String> classNames = new ArrayList<String>();
		try {
			InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("\\.", "/"));
			BufferedReader reader= new BufferedReader(new java.io.InputStreamReader(stream));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.endsWith(".class")) {
					String className = packageName + "." + line.substring(0, line.lastIndexOf(".class"));
					classNames.add(className);
				}else {
					classNames.addAll(packagExplorer(packageName + "." + line));
				}
			}
			return classNames;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
}
