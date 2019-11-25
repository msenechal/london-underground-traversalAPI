package org.neo4j.london.underground.morgan;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

import static java.lang.System.out;
import static org.neo4j.london.underground.morgan.Labels.Station;
import static org.neo4j.london.underground.morgan.Relationships.*;
import org.neo4j.london.underground.morgan.evaluator.*;

public class MySimpleTest {
    @Context
    public GraphDatabaseService db;

    @Procedure( name = "stations.getRoute" )
    public Stream<ValidRoute> getRoute(
            @Name("source") String source,
            @Name("dest") String dest
    ) {

        Node origin = db.findNode(Station, "name", source);
        Node destination = db.findNode(Station, "name", dest);

        out.println(origin.getLabels());
        out.println(destination.getLabels());

        try (Transaction tx = db.beginTx())
        {

            myEvaluator simpleEvaluator = new myEvaluator(destination);

            WeightedPath dijkstraTest = findShortestPath(origin, destination);
            out.println("dijkstra" + dijkstraTest);

            return db.traversalDescription()
                    .depthFirst()
                    .relationships( JUBILEE )
                    .relationships( METROPOLITAN)
                    .evaluator( Evaluators.toDepth( 10 ) )
                    .evaluator( simpleEvaluator )
                    .uniqueness(Uniqueness.NODE_PATH)
                    .traverse( origin )
                    .stream().map(ValidRoute::new);
        }
    }

    public WeightedPath findShortestPath( final Node nodeA, final Node nodeB )
    {
        // tag::dijkstraUsage[]
        PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(
                PathExpanders.forTypeAndDirection( JUBILEE, Direction.BOTH ), "time" );

        WeightedPath path = finder.findSinglePath( nodeA, nodeB );

        path.weight();
        // end::dijkstraUsage[]
        return path;
    }
}
