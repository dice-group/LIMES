package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.model.GetClassesTask;
import org.aksw.limes.core.gui.model.GetPropertiesTask;
import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;

public class TaskResultSerializerTest {

    KBInfo info;
    Model model;
    ClassMatchingNode testNode;

    @Before
    public void setUp() {
	info = new KBInfo();
	info.setEndpoint("testEndpoint");
	model = ModelFactory.createDefaultModel();
	try {
	    testNode = new ClassMatchingNode(new URI("testClass"), null);
	} catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerializingGetClassesTask() {
	GetClassesTask task = new GetClassesTask(info, model, null);
	List<ClassMatchingNode> input = new ArrayList<ClassMatchingNode>();
	input.add(testNode);
	TaskResultSerializer.serializeTaskResult(task, input);
	List<ClassMatchingNode> output = (List<ClassMatchingNode>) TaskResultSerializer.getTaskResult(task);
	assertEquals(input.hashCode(), output.hashCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerializingGetPropertiesTask() {
	GetPropertiesTask task = new GetPropertiesTask(info, model, testNode);
	List<String> input = new ArrayList<String>();
	input.add("testProperty");
	TaskResultSerializer.serializeTaskResult(task, input);
	List<String> output = TaskResultSerializer.getTaskResult(task);
	assertEquals(input.hashCode(), output.hashCode());
    }

}
