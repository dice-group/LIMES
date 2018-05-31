package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.io.config.KBInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PathResolver {

    public static final String RESOURCE_PREFIX = "src/main/resources/";

    public static String resolvePath(String path){
        if(path.startsWith(RESOURCE_PREFIX)) {
            path = path.replace(RESOURCE_PREFIX, "");
            String nPath = PathResolver.class.getClassLoader().getResource(path).getFile();
            if(Files.exists(Paths.get(nPath))){
                return nPath;
            }
        }
        return path;
    }

    public static void resolvePath(KBInfo info){
        info.setEndpoint(
                PathResolver.resolvePath(info.getEndpoint())
        );
    }

}
