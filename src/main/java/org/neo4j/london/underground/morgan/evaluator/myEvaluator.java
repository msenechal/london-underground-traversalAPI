package org.neo4j.london.underground.morgan.evaluator;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.PathEvaluator;

public class myEvaluator implements PathEvaluator<Double> {

    private final Node destination;

    public myEvaluator(Node destination) {
        this.destination = destination;
    }

    @Override
    public Evaluation evaluate(Path path, BranchState<Double> state) {

        if ( path.endNode().equals( destination ) ) {
            return Evaluation.INCLUDE_AND_PRUNE;
        }

        // give more functionalities like excluding stations that do not have disability access
        // list of exclusion (e.g I don't want to travel with central line...)
        // maximum time / delay

        // ensure when you change line that you don't go back to a line you've already been
        // max changes

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

    @Override
    public Evaluation evaluate(Path path) {
        return null;
    }
}
