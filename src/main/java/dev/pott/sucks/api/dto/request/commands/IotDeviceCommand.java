package dev.pott.sucks.api.dto.request.commands;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.request.portal.PortalIotCommandRequest.JsonPayloadHeader;
import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public abstract class IotDeviceCommand<RESPONSETYPE> {
    private final String xmlCommandName;
    private final String jsonCommandName;

    protected IotDeviceCommand(String xmlCommandName, String jsonCommandName) {
        this.xmlCommandName = xmlCommandName;
        this.jsonCommandName = jsonCommandName;
    }

    public String getName(boolean forXml) {
        return forXml ? xmlCommandName : jsonCommandName;
    }

    public boolean forceXmlFormat() {
        return false;
    }

    public String getPayload(Gson gson, boolean asXml) {
        if (asXml) {
            return String.format("<ctl td=\"%s\" />", xmlCommandName);
        }

        // JSON
        Map<String, Object> data = new HashMap<String, Object>();
        Map<String, String> args = getPayloadJsonArgs();
        data.put("header", new JsonPayloadHeader());
        if (args != null && !args.isEmpty()) {
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("data", args);
            data.put("body", body);
        }
        return gson.toJson(data).toString();
    }

    protected Map<String, String> getPayloadJsonArgs() {
        return null;
    }

    public abstract RESPONSETYPE convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception;

    protected Node getFirstXPathMatch(String xml, String xpathExpression)
            throws XPathExpressionException, NoSuchElementException {
        InputSource inputXML = new InputSource(new StringReader(xml));
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(xpathExpression, inputXML, XPathConstants.NODESET);
        if (nodes.getLength() == 0) {
            throw new NoSuchElementException();
        }
        return nodes.item(0);
    }
}
