package org.neo4j.london.underground.morgan;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.london.underground.morgan.Properties.*;

public class ValidRoute {
    public Path validPath;
    public double time;
    public List<Node> stations;
    public Number nodeZone;

    public ValidRoute(Path validPath, double time, Integer nodeZone){
        this.validPath = validPath;
        this.time = time;
        this.nodeZone = nodeZone;
    }

    public ValidRoute(Path validPath) {
        stations = new ArrayList<>();

        for ( Node station : validPath.nodes() ) {
            stations.add(station);
        }

        double time = 0d;

        for ( Relationship rel : validPath.relationships() ) {
            time += ((Number) rel.getProperty("time")).doubleValue();
        }

        for ( Node node : validPath.nodes() ) {
                nodeZone = (Number) node.getProperty(ZONE);
        }

        this.validPath = validPath;
        this.time = time;
    }
}
