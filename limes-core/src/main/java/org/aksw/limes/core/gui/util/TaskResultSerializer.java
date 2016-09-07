package org.aksw.limes.core.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskResultSerializer {

    public static File folder = new File("");
    static Logger logger = LoggerFactory.getLogger(TaskResultSerializer.class.getName());
    
    /** 
     * Checks if the task result was serialized by checking the hashcode of the task
     * @param task GetClassesTask or GetPropertiesTask object
     * @return result of the task or null if it was not serialized
     */
    @SuppressWarnings("rawtypes")
    public static List getTaskResult(Task task){
	String hash = task.hashCode() + "";
        File serializationFile = new File(folder + "cache/" + hash + ".ser");
        logger.info("Checking for file " + serializationFile.getAbsolutePath());
        List taskResult = null;
        try {
            if (serializationFile.exists()) {
                logger.info("Found serialization. Loading data from file " + serializationFile.getAbsolutePath());
                FileInputStream fileIn = new FileInputStream(serializationFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                taskResult = (List) in.readObject();
                in.close();
                fileIn.close();
            }else{
        	return null;
            }
            if (taskResult.size() == 0) {
                throw new Exception();
            } else {
                logger.info("Serialization loaded successfully from file " + serializationFile.getAbsolutePath());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	return taskResult;
    }
    
    @SuppressWarnings("rawtypes")
    public static void serializeTaskResult(Task task, List result){
	 try{
	String hash = task.hashCode() + "";
        File serializationFile = new File(folder + "cache/" + hash + ".ser");
	         FileOutputStream fileOut = new FileOutputStream(serializationFile);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(result);
	         out.close();
	         fileOut.close();
	         logger.info("Serialized data is saved in " + serializationFile.getAbsolutePath());
	      }catch(IOException i)
	      {
	          i.printStackTrace();
	      }
    }
}
