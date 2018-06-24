package org.aksw.limes.core.io.describe;

import org.aksw.limes.core.io.config.KBInfo;

import java.util.List;
import java.util.stream.Stream;

public interface IDescriptor {

    public IResourceDescriptor describe(String s);

    public IResourceDescriptor describe(String s, int recursion);

    public List<IResourceDescriptor> describeAll(Iterable<String> uris, int recursion);

    public List<IResourceDescriptor> describeAll(Iterable<String> uris);

    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris, int recursion);

    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris);

}
