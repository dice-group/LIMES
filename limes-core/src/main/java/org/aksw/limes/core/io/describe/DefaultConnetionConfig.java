package org.aksw.limes.core.io.describe;

public class DefaultConnetionConfig implements IConnectionConfig {

    @Override
    public int getRequestDelayInMs() {
        return 50;
    }

}
