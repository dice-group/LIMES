package org.hobbit.sdk.examples.examplebenchmark;

import org.hobbit.core.components.Component;
import org.hobbit.sdk.ComponentsExecutor;
import org.hobbit.sdk.EnvironmentVariablesWrapper;
import org.hobbit.sdk.JenaKeyValue;
import org.hobbit.sdk.docker.AbstractDockerizer;
import org.hobbit.sdk.docker.RabbitMqDockerizer;
import org.hobbit.sdk.docker.builders.*;
import org.hobbit.sdk.docker.builders.PullBasedDockersBuilder;
import org.hobbit.sdk.docker.builders.hobbit.*;
import org.hobbit.sdk.examples.examplebenchmark.system.SystemAdapter;
import org.hobbit.sdk.utils.CommandQueueListener;
import org.hobbit.sdk.utils.commandreactions.MultipleCommandsReaction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static org.hobbit.sdk.CommonConstants.*;
import static org.hobbit.sdk.examples.examplebenchmark.Constants.*;


/**
 * @author Pavel Smirnov
 *
 * This test shows how to debug your system under already published benchmark images
 * if docker images of benchmarkController components are available online
 *
 *
 */


public class ExampleSystemTest extends EnvironmentVariablesWrapper {

    private RabbitMqDockerizer rabbitMqDockerizer;
    private ComponentsExecutor componentsExecutor;
    private CommandQueueListener commandQueueListener;




    BenchmarkDockerBuilder benchmarkBuilder;
    DataGenDockerBuilder dataGeneratorBuilder;
    TaskGenDockerBuilder taskGeneratorBuilder;
    EvalStorageDockerBuilder evalStorageBuilder;
    SystemAdapterDockerBuilder systemAdapterBuilder;
    EvalModuleDockerBuilder evalModuleBuilder;


    public void init(boolean useCachedImages) throws Exception {

        benchmarkBuilder = new BenchmarkDockerBuilder(new PullBasedDockersBuilder(BENCHMARK_IMAGE_NAME));
        dataGeneratorBuilder = new DataGenDockerBuilder(new PullBasedDockersBuilder(DATAGEN_IMAGE_NAME));
        taskGeneratorBuilder = new TaskGenDockerBuilder(new PullBasedDockersBuilder(TASKGEN_IMAGE_NAME));
        evalStorageBuilder = new EvalStorageDockerBuilder(new PullBasedDockersBuilder(EVAL_STORAGE_IMAGE_NAME));
        evalModuleBuilder = new EvalModuleDockerBuilder(new PullBasedDockersBuilder(EVALMODULE_IMAGE_NAME));

        systemAdapterBuilder = new SystemAdapterDockerBuilder(new ExampleDockersBuilder(SystemAdapter.class, SYSTEM_IMAGE_NAME).useCachedImage(useCachedImages));

    }

    @Test
    @Ignore
    public void buildImages() throws Exception {
        init(false);
        systemAdapterBuilder.build().prepareImage();
    }

    @Test
    public void checkHealth() throws Exception {
        checkHealth(false);
    }

    @Test
    public void checkHealthDockerized() throws Exception {
        checkHealth(true);
    }


    private void checkHealth(boolean dockerize) throws Exception {

        Boolean useCachedImages = true;

        init(useCachedImages);

        rabbitMqDockerizer = RabbitMqDockerizer.builder().build();

        setupCommunicationEnvironmentVariables(rabbitMqDockerizer.getHostName(), "session_"+String.valueOf(new Date().getTime()));
        setupBenchmarkEnvironmentVariables(EXPERIMENT_URI, createBenchmarkParameters());
        setupGeneratorEnvironmentVariables(1,1);
        setupSystemEnvironmentVariables(SYSTEM_URI, createSystemParameters());

        commandQueueListener = new CommandQueueListener();
        componentsExecutor = new ComponentsExecutor(commandQueueListener, environmentVariables);

        rabbitMqDockerizer.run();

        Component benchmarkController = benchmarkBuilder.build();
        Component dataGen = dataGeneratorBuilder.build();
        Component taskGen = taskGeneratorBuilder.build();
        Component evalStorage = evalStorageBuilder.build();
        Component evalModule = evalModuleBuilder.build();
        Component systemAdapter = new SystemAdapter();

        if(dockerize)
            systemAdapter = systemAdapterBuilder.build();

        commandQueueListener.setCommandReactions(
                new MultipleCommandsReaction(componentsExecutor, commandQueueListener)
                        .dataGenerator(dataGen).dataGeneratorImageName(dataGeneratorBuilder.getImageName())
                        .taskGenerator(taskGen).taskGeneratorImageName(taskGeneratorBuilder.getImageName())
                        .evalStorage(evalStorage).evalStorageImageName(evalStorageBuilder.getImageName())
                        .evalModule(evalModule).evalModuleImageName(evalModuleBuilder.getImageName())
                        .systemContainerId(systemAdapterBuilder.getImageName())
        );

        componentsExecutor.submit(commandQueueListener);
        commandQueueListener.waitForInitialisation();

        componentsExecutor.submit(benchmarkController);
        componentsExecutor.submit(systemAdapter, systemAdapterBuilder.getImageName());

        commandQueueListener.waitForTermination();

        rabbitMqDockerizer.stop();

        Assert.assertFalse(componentsExecutor.anyExceptions());
    }

    public JenaKeyValue createBenchmarkParameters() {
        JenaKeyValue kv = new JenaKeyValue();
        kv.setValue(BENCHMARK_URI+"/param1", "value1");
        return kv;
    }

    private static JenaKeyValue createSystemParameters(){
        JenaKeyValue kv = new JenaKeyValue();
        kv.setValue(SYSTEM_URI+"/param1", "value1");
        return kv;
    }

}
