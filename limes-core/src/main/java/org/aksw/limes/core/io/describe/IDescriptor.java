package org.aksw.limes.core.io.describe;

import java.util.List;
import java.util.stream.Stream;


/**
 * A descriptor gives access to the concise bounded description (CBD) of resources.
 * Because a full CBD is often not feasible or needed the depth of recursive description calls is bounded.
 * However, the descriptor expand every resource not only empty nodes.
 *
 * @author Cedric Richter
 */
public interface IDescriptor {

    /**
     * CBD of a single resource without recursive expansion
     * @param s the uri for the resource to query
     * @return a resource description for s (will never return null)
     */
    public IResourceDescriptor describe(String s);

    /**
     * CBD of a single resource with recursive expansion of a given depth
     * @param s the uri for the resource to query
     * @param recursion the number of recursive expansion
     * @return a resource description for s (will never return null)
     */
    public IResourceDescriptor describe(String s, int recursion);

    /**
     * Describe many uris at once with recursive expansion
     * @param uris the uris for the resource to query
     * @param recursion the number of recursive expansion
     * @return all resource descriptions for uris (will never return null)
     */
    public List<IResourceDescriptor> describeAll(Iterable<String> uris, int recursion);

    /**
     * Describe many uris at once without recursive expansion
     * @param uris the uris for the resource to query
     * @return all resource descriptions for uris (will never return null)
     */
    public List<IResourceDescriptor> describeAll(Iterable<String> uris);


    /**
     * Describe many uris at once with recursive expansion in a stream
     * @param uris the uris for the resource to query
     * @param recursion the number of recursive expansion
     * @return all resource descriptions for uris (will never return null)
     */
    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris, int recursion);

    /**
     * Describe many uris at once without recursive expansion in a stream
     * @param uris the uris for the resource to query
     * @return all resource descriptions for uris (will never return null)
     */
    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris);

}
