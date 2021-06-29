package path;

import cgf.CFG;
import cgf.CFGNode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class PathGenerator {

    public static final int MAX_PATH_LENGTH = 20;

    public List<GraphPath<String, DefaultEdge>> getAllPathsWithIdsFromCFG(final CFG cfg) {
        DefaultDirectedGraph<String, DefaultEdge> directedGraph
                = new DefaultDirectedGraph<>(DefaultEdge.class);

        cfg.getNodes().keySet().forEach(directedGraph::addVertex);
        cfg.getEdges()
           .values()
           .forEach(cfgEdge -> directedGraph.addEdge(cfgEdge.getFrom().getId(), cfgEdge.getTo().getId()));

        final AllDirectedPaths<String, DefaultEdge> pathAnalyser = new AllDirectedPaths<>(directedGraph);

        final List<GraphPath<String, DefaultEdge>> allPaths =
                pathAnalyser.getAllPaths("root", "end", false, MAX_PATH_LENGTH);

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

        final AllDirectedPaths<CFGNode, DefaultEdge> pathAnalyser = new AllDirectedPaths<>(directedGraph);

        final List<GraphPath<CFGNode, DefaultEdge>> allPaths = pathAnalyser.getAllPaths(cfg.getRootNode(),
                cfg.getEndNode(),
                false,
                MAX_PATH_LENGTH);

        allPaths.forEach(System.out::println);
        return allPaths;
    }
}
