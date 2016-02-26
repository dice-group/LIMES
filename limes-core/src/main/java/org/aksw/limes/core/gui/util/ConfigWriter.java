package org.aksw.limes.core.gui.util;

import java.io.File;
import java.io.FileOutputStream;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.io.config.KBInfo;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
/**
 * Class for saving LIMES config to file
 * 
 * @author Manuel Jacob
 */
public class ConfigWriter {
	public static void saveToXML(Config config, File file) {
		// Basierend auf Code aus SAIM.
		try {
			Element rootElement = new Element("LIMES");

			config.getPrefixes().forEach((prefix, namespace) -> {
				Element prefixElement = new Element("PREFIX");
				prefixElement.addContent(new Element("NAMESPACE")
						.setText(namespace));
				prefixElement.addContent(new Element("LABEL").setText(prefix));
				rootElement.addContent(prefixElement);
			});

			Element sourceElement = new Element("SOURCE");
			fillKBElement(sourceElement, config.getSourceInfo());
			rootElement.addContent(sourceElement);
			Element targetElement = new Element("TARGET");
			fillKBElement(targetElement, config.getTargetInfo());
			rootElement.addContent(targetElement);

			rootElement.addContent(new Element("METRIC")
					.setText(config.getMetricExpression()));

			Element acceptanceElement = new Element("ACCEPTANCE");
			acceptanceElement
					.addContent(new Element("THRESHOLD").setText(Double
							.toString(config.getAcceptanceThreshold())));
			acceptanceElement.addContent(new Element("FILE")
					.setText(config.getAcceptanceFile()));
			acceptanceElement.addContent(new Element("RELATION")
					.setText(config.getAcceptanceRelation()));
			rootElement.addContent(acceptanceElement);

			Element reviewElement = new Element("REVIEW");
			reviewElement.addContent(new Element("THRESHOLD").setText(Double
					.toString(config.getVerificationThreshold())));
			reviewElement.addContent(new Element("FILE")
					.setText(config.getVerificationFile()));
			reviewElement.addContent(new Element("RELATION")
					.setText(config.getVerificationRelation()));
			rootElement.addContent(reviewElement);

			rootElement.addContent(new Element("EXECUTION")
					.setText(config.getExecutionPlan()));
			rootElement.addContent(new Element("GRANULARITY").setText(Integer
					.toString(config.getGranularity())));
			rootElement.addContent(new Element("OUTPUT")
					.setText(config.getOutputFormat()));

			Document document = new Document(rootElement, new DocType("LIMES",
					"limes.dtd"));
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(document, new FileOutputStream(file, false));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void fillKBElement(Element element, KBInfo kb) {
		element.addContent(new Element("ID").setText(kb.getId()));
		element.addContent(new Element("ENDPOINT").setText(kb.getEndpoint()));
		element.addContent(new Element("GRAPH").setText(kb.getGraph()));
		element.addContent(new Element("VAR").setText(String.valueOf(kb.getVar())));
		element.addContent(new Element("PAGESIZE").setText(String
				.valueOf(kb.getPageSize())));
		for (String restriction : kb.getRestrictions()) {
			if (restriction != null && restriction.trim().length() > 0) {
				element.addContent(new Element("RESTRICTION")
						.setText(restriction));
			}
		}
		for (String property : kb.getProperties()) {
			if (property != null && property.trim().length() > 0) {
				element.addContent(new Element("PROPERTY").setText(property));
			}
		}
		if (kb.getType() != null && kb.getType().length() > 0) {
			element.addContent(new Element("TYPE").setText(kb.getType()));
		}
	}
}
