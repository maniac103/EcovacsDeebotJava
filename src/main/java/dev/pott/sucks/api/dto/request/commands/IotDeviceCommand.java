package dev.pott.sucks.api.dto.request.commands;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.google.gson.Gson;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public abstract class IotDeviceCommand<RESPONSETYPE> {
    private final String commandName;

    protected IotDeviceCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getName() {
        return commandName;
    }

    public String getPayloadXml() {
        return String.format("<ctl td=\"{}\" />", commandName);
    }

    public abstract RESPONSETYPE convertResponse(String responsePayload, Gson gson) throws Exception;

    protected Node getFirstXPathMatch(String xml, String xpathExpression) throws XPathExpressionException, NoSuchElementException {
        InputSource inputXML = new InputSource(new StringReader(xml));
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpathExpression, inputXML, XPathConstants.NODESET);
        if (nodes.getLength() == 0) {
            throw new NoSuchElementException();
        }
        return nodes.item(0);
    }
}
