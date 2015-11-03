package org.aksw.limes.core.model;

import org.apache.commons.codec.digest.DigestUtils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 * @version 2015-11-03
 * 
 */
public class Link extends Triple implements Comparable<Link> {
	
	private String hash;

	public Link(Node s, Node p, Node o) {
		super(s, p, o);
		this.hash = DigestUtils.shaHex(s.getURI() + "\n" + p.getURI() + "\n" + o.toString());
	}
	
	public Link(RDFNode s, RDFNode p, RDFNode o) {
		this(s.asNode(), p.asNode(), o.asNode());
	}

	@Override
	public int compareTo(Link o) {
		return this.hash.compareTo(o.getHash());
	}

	public String getHash() {
		return hash;
	}

}
