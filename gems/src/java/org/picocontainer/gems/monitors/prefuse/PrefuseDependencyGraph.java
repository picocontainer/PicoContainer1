package org.picocontainer.gems.monitors.prefuse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.picocontainer.gems.monitors.ComponentDependencyMonitor.Dependency;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;

public class PrefuseDependencyGraph implements ComponentDependencyListener {
    private Graph graph;

    private Map nodes;

    public PrefuseDependencyGraph() {
        this.graph = initializeGraph();
        this.nodes = new HashMap();
    }

    public void addDependency(Dependency dependency) {
        Node componentNode = addNode(dependency.getComponent());
        Node dependencyNode = addNode(dependency.getDependency());
        if (dependencyNode != null) {
            graph.addEdge(componentNode, dependencyNode);
        }
    }

    Collection getTypes() {
        return nodes.keySet();
    }

    Node[] getNodes() {
        return (Node[]) nodes.values().toArray(new Node[nodes.size()]);
    }

    private Node addNode(Class type) {
        if (type != null && !nodes.containsKey(type)) {
            Node node = graph.addNode();
            node.set("type", type);
            nodes.put(type, node);
        }
        return (Node) nodes.get(type);
    }

    private Graph initializeGraph() {
        return getGraph(getSchema());
    }

    private Graph getGraph(Schema schema) {
        graph = new Graph(true);
        graph.addColumns(schema);
        return graph;
    }

    private Schema getSchema() {
        Schema schema = new Schema();
        schema.addColumn("type", Class.class, null);
        return schema;
    }

    public TupleSet getEdges() {
        return graph.getEdges();
    }

    public Graph getGraph() {
        return graph;
    }
}