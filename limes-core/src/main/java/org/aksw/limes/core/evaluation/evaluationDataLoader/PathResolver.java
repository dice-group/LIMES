package org.aksw.limes.core.evaluation.evaluationDataLoader;

import org.aksw.limes.core.io.config.KBInfo;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PathResolver {

    public static final String RESOURCE_PREFIX = "src/main/resources/";

    public static String resolvePath(String path){
        if(path.startsWith(RESOURCE_PREFIX)) {
            path = path.replace(RESOURCE_PREFIX, "");
            String nPath = PathResolver.class.getClassLoader().getResource(path).getFile();
            if(Files.exists(Paths.get(nPath))){
                return nPath;
            }else{
                return syncResource(path);
            }
        }
        return path;
    }

    public static void resolvePath(KBInfo info){
        info.setEndpoint(
                PathResolver.resolvePath(info.getEndpoint())
        );
    }

    public static String syncResource(String path){
        if(!Files.exists(Paths.get(path))) {
            InputStream inputStream = DataSetChooser2.class.getClassLoader().getResourceAsStream(path);

            if(inputStream != null){
                try {
                    Path p = Files.createTempFile("jarFile", ".tmp");
                    Files.copy(inputStream, p, StandardCopyOption.REPLACE_EXISTING);
                    IOUtils.closeQuietly(inputStream);
                    return p.toString();
                } catch (IOException e) {
                }
            }

        }
        return path;
    }


}
