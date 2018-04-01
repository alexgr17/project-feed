package ru.alexgryaznov.flproject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.alexgryaznov.flproject.domain.Category;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.util.NodeListUtil;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Component
public class RssParserService {

    private static final String ITEM_XPATH = "/rss/channel/item";
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy hh:mm:ss";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate;

    public RssParserService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public List<Project> loadProjects(String urlString) {

        final String rss = restTemplate.getForObject(urlString, String.class);
        final InputStream inputStream = new ByteArrayInputStream(rss.getBytes());

        final Document document;
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            log.error("Error while parsing XML from url: {}", urlString);
            throw new IllegalStateException(e);
        }

        final XPath xPath = XPathFactory.newInstance().newXPath();

        final NodeList nodeList;
        try {
            nodeList = (NodeList) xPath.compile(ITEM_XPATH).evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Error while getting items by xPath");
            throw new IllegalStateException(e);
        }

        return NodeListUtil.stream(nodeList)
                .map(node -> getProject((Element) node))
                .collect(Collectors.toList());
    }

    private Project getProject(Element element) {
        final Project project = new Project();
        project.setTitle(getNodeValue(element, "title"));
        project.setLink(getNodeValue(element, "link"));
        project.setDescription(getNodeValue(element, "description"));
        project.setContent(getNodeValue(element, "content"));
        project.setGuid(getNodeValue(element, "guid"));
        project.setCategories(getCategories(element));
        project.setPubDate(parseDate(getNodeValue(element, "pubDate")));
        return project;
    }

    private String getNodeValue(Element element, String name) {
        final NodeList nodeList = element.getElementsByTagName(name);
        switch (nodeList.getLength()) {
            case 0:
                return null;
            case 1:
                return nodeList.item(0).getFirstChild().getNodeValue();
            default:
                throw new IllegalStateException("Ambiguous node list length: {}" + nodeList.getLength() + ", name: " + name);
        }
    }

    private List<Category> getCategories(Element element) {
        return NodeListUtil.stream(element.getElementsByTagName("category"))
                .map(Node::getFirstChild)
                .map(Node::getNodeValue)
                .map(value -> {
                    final Category category = new Category();
                    category.setTitle(value);
                    return category;
                })
                .collect(Collectors.toList());
    }

    private Date parseDate(String dateWithTimezone) {
        if (dateWithTimezone == null) {
            return null;
        }

        final int timezonePosition = dateWithTimezone.lastIndexOf(" ");
        final String date = dateWithTimezone.substring(0, timezonePosition).trim();
        final String timezone = dateWithTimezone.substring(timezonePosition, dateWithTimezone.length()).trim();

        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            log.error("Error while parsing date: {}", dateWithTimezone);
            throw new IllegalStateException(e);
        }
    }
}
