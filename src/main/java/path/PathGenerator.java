package path;

import graph.CFG;
import graph.CFGNode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class PathGenerator {

    public List<GraphPath<String, DefaultEdge>> getAllPathsWithIdsFromCFG(final CFG cfg) {
        DefaultDirectedGraph<String, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        cfg.getNodes().keySet().forEach(directedGraph::addVertex);
        cfg.getEdges()
           .values()
           .forEach(cfgEdge -> directedGraph.addEdge(cfgEdge.getFrom().getId(), cfgEdge.getTo().getId()));

        AllDirectedPaths<String, DefaultEdge> pathAnalyser = new AllDirectedPaths<String, DefaultEdge>(directedGraph);

        final List<GraphPath<String, DefaultEdge>> allPaths = pathAnalyser.getAllPaths("root", "end", false, 20);

        allPaths.forEach(System.out::println);

        return allPaths;
    }

    public List<GraphPath<CFGNode, DefaultEdge>> getAllPathsFromCFG(final CFG cfg) {
        DefaultDirectedGraph<CFGNode, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        cfg.getNodes().values().forEach(directedGraph::addVertex);
        cfg.getEdges()
           .values()
           .forEach(cfgEdge -> directedGraph.addEdge(cfgEdge.getFrom(), cfgEdge.getTo()));

        AllDirectedPaths<CFGNode, DefaultEdge> pathAnalyser = new AllDirectedPaths<CFGNode, DefaultEdge>(directedGraph);

        final List<GraphPath<CFGNode, DefaultEdge>> allPaths = pathAnalyser.getAllPaths(cfg.getRootNode(),
                cfg.getEndNode(),
                false,
                20);

        allPaths.forEach(System.out::println);
        return allPaths;
    }
}
