package ru.alexgryaznov.flproject.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class NodeListUtil {

    private NodeListUtil() {
        // empty
    }

    public static Stream<Node> stream(NodeList nodeList) {
        final List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes.stream();
    }
}
