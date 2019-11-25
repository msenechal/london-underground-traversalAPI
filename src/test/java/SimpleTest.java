import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.london.underground.morgan.MySimpleTest;

import java.io.File;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class SimpleTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure( MySimpleTest.class )
            .withFixture("CREATE CONSTRAINT ON (node:Station) ASSERT (node.name) IS UNIQUE")
            .withFixture(
                    new File(
                            getClass().getClassLoader().getResource("sampleTest.cypher").getFile()
                    )

            );

    @Test
    public void getRoute() {
        GraphDatabaseService db = neo4j.getGraphDatabaseService();

        try ( Transaction tx = db.beginTx() ) {
            Result res = db.execute( "CALL stations.getRoute('Kingsbury','West Hampstead')" );
            assertTrue(res.hasNext());
            while ( res.hasNext() ) {
                Map<String, Object> row = res.next();
                System.out.println(row.get("validPath") + " - Estimated time : " + row.get("time"));
            }
        }
    }
}
