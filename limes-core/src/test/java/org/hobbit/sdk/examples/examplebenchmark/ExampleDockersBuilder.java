package org.hobbit.sdk.examples.examplebenchmark;


import org.hobbit.sdk.docker.builders.DynamicDockerFileBuilder;

import static org.hobbit.sdk.examples.examplebenchmark.Constants.*;

/**
 * @author Pavel Smirnov
 */

public class ExampleDockersBuilder extends DynamicDockerFileBuilder {
	
	
	public ExampleDockersBuilder(Class runnerClass, String imageName) throws Exception {
		super("ExampleDockersBuilder");
		imageName(imageName);
		//name for searching in logs
		containerName(runnerClass.getSimpleName());
		//temp docker file will be created there
		buildDirectory(SDK_BUILD_DIR_PATH);
		//should be packaged will all dependencies (via 'mvn package -DskipTests=true' command)
		jarFilePath(SDK_JAR_FILE_PATH);
		//will be placed in temp dockerFile
		dockerWorkDir(SDK_WORK_DIR_PATH);
		//will be placed in temp dockerFile
		runnerClass(org.hobbit.core.run.ComponentStarter.class, runnerClass);
	}
	
}
