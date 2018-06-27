package org.aksw.limes.core.evaluation.evaluationDataLoader.datasets;

import org.aksw.limes.core.evaluation.evaluationDataLoader.IDataSetIO;
import org.aksw.limes.core.evaluation.evaluationDataLoader.PathResolver;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class helps fix the issue concerning with Paths of DirectoryName, FileName and prepPropertyFile.
 * This class also helps loading local files from a jar file
 * @author Cedric Richter
 *
 */

public class PathResolvingIO implements IDataSetIO {

    private IDataSetIO delegate;

    public PathResolvingIO(IDataSetIO delegate) {
        this.delegate = delegate;
    }


    private String dirname(String s){
        Path p = Paths.get(s);
        Path o = null;

        for(int i = 0; i < p.getNameCount()-1; i++){
            Path t = p.getName(i);
            if(o == null){
                o = t;
            }else{
                o = o.resolve(t);
            }
        }

        return "/"+o.toString()+"/";
    }


    private String filename(String s){
        Path p = Paths.get(s);
        return p.getName(p.getNameCount()-1).toString();
    }

    private String prepPropertyFile(String name){
        if (name.indexOf("/") > 0)
            name = name.substring(name.lastIndexOf("/"));
        String filename = name.substring(0, name.lastIndexOf("."));
        filename += "propertymatch";
        return filename;
    }

    private String postPropertyFile(String path){
        if(path.endsWith(".tmp")){
            String nPath = path.substring(0, path.lastIndexOf(".")) + "propertymatch";
            try {
                Files.move(Paths.get(path), Paths.get(nPath));
            } catch (IOException e) {
            }
        }
        return path;
    }


    /**
     * Takes baseFolder and ConfigFiles as Arguments and
     * @returns Properties from fixed file directory and filename
     */
    @Override
    public PropertyMapping loadProperties(String baseFolder, String configFile) {
        String path = PathResolver.resolvePath(baseFolder+prepPropertyFile(configFile));
        postPropertyFile(path);
        return delegate.loadProperties(dirname(path), filename(path));
    }
    /**
     * Takes path, configFile and type as Arguments and
     * @returns sourcecahce loaded with data based on function arguments
     */
    @Override
    public ACache loadSourceCache(Configuration cfg, String path, String type) {
        return delegate.loadSourceCache(cfg, PathResolver.resolvePath(path), type);
    }

    /**
     * Takes path, configFile and type as Arguments and
     * @returns TargetCahce loaded with data based on function arguments
     */
    @Override
    public ACache loadTargetCache(Configuration cfg, String path, String type) {
        return delegate.loadTargetCache(cfg, PathResolver.resolvePath(path), type);
    }

    /**
     * Takes path and configFileas Arguments and
     * @returns Mapping with data based on function arguments
     */
    @Override
    public AMapping loadMapping(Configuration cfg, String path) {
        return delegate.loadMapping(cfg, PathResolver.resolvePath(path));
    }
}
