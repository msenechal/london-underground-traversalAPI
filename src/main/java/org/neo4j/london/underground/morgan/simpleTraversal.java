/*
 * Licensed to Neo4j under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo4j licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
// tag::sampleDocumentation[]
// tag::_sampleDocumentation[]
package org.neo4j.london.underground.morgan;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.io.fs.FileUtils;

import static java.lang.System.out;

public class simpleTraversal
{
    private GraphDatabaseService db;
    private TraversalDescription friendsTraversal;

    private static final File databaseDirectory = new File( "target/neo4j-traversal-example" );

    public static void main( String[] args ) throws IOException
    {
        FileUtils.deleteRecursively( databaseDirectory );
        GraphDatabaseService database = new GraphDatabaseFactory().newEmbeddedDatabase( databaseDirectory );
        simpleTraversal example = new simpleTraversal( database );
        Node joe = example.createData();
        example.run( joe );
        database.shutdown();
    }

    public simpleTraversal( GraphDatabaseService db )
    {
        this.db = db;
        // tag::basetraverser[]
        friendsTraversal = db.traversalDescription()
                .depthFirst()
                .relationships( Rels.KNOWS )
                .uniqueness( Uniqueness.RELATIONSHIP_GLOBAL );
        // end::basetraverser[]
    }

    private Node createData()
    {
        String query = "CREATE (joe {name: 'Joe'}), (sara {name: 'Sara'}), "
                + "(lisa {name: 'Lisa'}), (peter {name: 'PETER'}), (dirk {name: 'Dirk'}), "
                + "(lars {name: 'Lars'}), (ed {name: 'Ed'}),"
                + "(joe)-[:KNOWS]->(sara), (lisa)-[:LIKES]->(joe), "
                + "(peter)-[:KNOWS]->(sara), (dirk)-[:KNOWS]->(peter), "
                + "(lars)-[:KNOWS]->(drk), (ed)-[:KNOWS]->(lars), "
                + "(lisa)-[:KNOWS]->(lars) "
                + "RETURN joe";
        Result result = db.execute( query );
        Object joe = result.columnAs( "joe" ).next();
        if ( joe instanceof Node )
        {
            return (Node) joe;
        }
        else
        {
            throw new RuntimeException( "Joe isn't a node!" );
        }
    }

    private void run( Node joe )
    {
        try (Transaction tx = db.beginTx())
        {
            out.println( knowsLikesTraverser( joe ) );
            out.println( traverseBaseTraverser( joe ) );
            out.println( depth3( joe ) );
            out.println( depth4( joe ) );
            out.println( nodes( joe ) );
            out.println( relationships( joe ) );
        }
    }

    public String knowsLikesTraverser( Node node )
    {
        String output = "";
        // tag::knowslikestraverser[]
        for ( Path position : db.traversalDescription()
                .depthFirst()
                .relationships( Rels.KNOWS )
                .relationships( Rels.LIKES, Direction.INCOMING )
                .evaluator( Evaluators.toDepth( 5 ) )
                .traverse( node ) )
        {
            output += position + "\n";
        }
        // end::knowslikestraverser[]
        return output;
    }

    public String traverseBaseTraverser( Node node )
    {
        String output = "";
        // tag::traversebasetraverser[]
        for ( Path path : friendsTraversal.traverse( node ) )
        {
            output += path + "\n";
        }
        // end::traversebasetraverser[]
        return output;
    }

    public String depth3( Node node )
    {
        String output = "";
        // tag::depth3[]
        for ( Path path : friendsTraversal
                .evaluator( Evaluators.toDepth( 3 ) )
                .traverse( node ) )
        {
            output += path + "\n";
        }
        // end::depth3[]
        return output;
    }

    public String depth4( Node node )
    {
        String output = "";
        // tag::depth4[]
        for ( Path path : friendsTraversal
                .evaluator( Evaluators.fromDepth( 2 ) )
                .evaluator( Evaluators.toDepth( 4 ) )
                .traverse( node ) )
        {
            output += path + "\n";
        }
        // end::depth4[]
        return output;
    }

    public String nodes( Node node )
    {
        String output = "";
        // tag::nodes[]
        for ( Node currentNode : friendsTraversal
                .traverse( node )
                .nodes() )
        {
            output += currentNode.getProperty( "name" ) + "\n";
        }
        // end::nodes[]
        return output;
    }

    public String relationships( Node node )
    {
        String output = "";
        // tag::relationships[]
        for ( Relationship relationship : friendsTraversal
                .traverse( node )
                .relationships() )
        {
            output += relationship.getType().name() + "\n";
        }
        // end::relationships[]
        return output;
    }

    // tag::sourceRels[]
    private enum Rels implements RelationshipType
    {
        LIKES, KNOWS
    }
    // end::sourceRels[]
}