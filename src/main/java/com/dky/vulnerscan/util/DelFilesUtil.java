package com.dky.vulnerscan.util;

import java.io.File;

public class DelFilesUtil {
	
	 public static void clearFiles(String workspaceRootPath){
	       File file = new File(workspaceRootPath);
	       if(file.exists()){
	           deleteFile(file);
	      }
	 }
	 private static void deleteFile(File file){
	      if(file.isDirectory()){
	           File[] files = file.listFiles();
	           for(int i=0; i<files.length; i++){
	                deleteFile(files[i]);
	           }
	      }
	      file.delete();
	 }
}
