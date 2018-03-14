package org.aksw.limes.core.io.preprocessing;

import java.util.Arrays;
import java.util.Map;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NEWPreprocessor {
	static Logger logger = LoggerFactory.getLogger(NEWPreprocessor.class.getName());
	

	public static ACache applyFunctionsToCache(ACache cache, Map<String, Map<String, String>> functions) {
		ACache cacheClone = cache.clone();
		functions.forEach((property, innerMap) -> {
			innerMap.forEach((propertyDub, functionChain) -> {
				System.out.println(property);
				for(Instance inst : cacheClone.getAllInstances()){
                    logger.debug("Function chain = " + functionChain);
                    applyRenameIfNecessary(inst, property, propertyDub);
                    if (functionChain != null) {
                        if (!functionChain.equals("")) {
                            String split[] = functionChain.split("->");
                            for (int i = 0; i < split.length; i++) {
                                String functionId = getFunctionId(split[i]);
                                PreprocessingFunctionType type = PreprocessingFunctionFactory
                                        .getPreprocessingType(functionId);
                                APreprocessingFunction func = PreprocessingFunctionFactory.getPreprocessingFunction(type);
                                String[] arguments = func.retrieveArguments(split[i]);
                                if (arguments.length > 0) {
                                    func.applyFunction(inst, propertyDub, arguments);
                                } else {
                                    func.applyFunction(inst, propertyDub);
                                }
                            }
                        }
                    }
				}
			});
		});
		return cacheClone;
	}

	public static void applyRenameIfNecessary(Instance inst, String property, String propertyDub) {
		if (property != null && !property.equals("") && propertyDub != null && !propertyDub.equals("")
				&& !property.equals(propertyDub)) {
			PreprocessingFunctionType type = PreprocessingFunctionFactory
					.getPreprocessingType(PreprocessingFunctionFactory.RENAME_PROPERTY);
			APreprocessingFunction func = PreprocessingFunctionFactory.getPreprocessingFunction(type);
			func.applyFunction(inst, property, propertyDub);
		}
	}
	
	public static String getFunctionId(String argString){
		return argString.split("\\(")[0];
	}

}
