package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.aksw.limes.core.measures.measure.customGraphs.description.IDescriptionGraphView;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.ILabel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Cedric Richter
 */
public class DescriptionGraphFactory {

    private LoadingHook hook;
    private IGraphLoaded base;
    private List<Consumer<ILabel>> listener = new ArrayList<>();

    private DescriptionGraphFactory(LoadingHook hook, IGraphLoaded base){
        this.hook = hook;
        this.base = base;
    }

    public static DescriptionGraphFactory lazy(IResourceDescriptor descriptor){
        LoadingHook hook = new LoadingHook(descriptor);
        return new DescriptionGraphFactory(hook, new LazyDescriptionGraphView(hook));
    }

    public static DescriptionGraphFactory eager(IResourceDescriptor descriptor){
        LoadingHook hook = new LoadingHook(descriptor);
        return new DescriptionGraphFactory(hook, new EagerDescriptionGraphView(hook));
    }

    public DescriptionGraphFactory listen(Consumer<ILabel> consumer){
        listener.add(consumer);
        return this;
    }

    public DescriptionGraphFactory listen(Collection<? extends Consumer<ILabel>> consumer){
        listener.addAll(consumer);
        return this;
    }

    private void applyListener(){
        if(!listener.isEmpty())
        this.base = new ListeningDescriptionGraphView(this.base, this.listener);
    }

    public IDescriptionGraphView build(){
        applyListener();
        hook.injectStart(base);
        return base;
    }

}
