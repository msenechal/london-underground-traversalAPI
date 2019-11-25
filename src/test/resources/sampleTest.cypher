CREATE (:Station:Zone4 {name:'Kingsbury', zone:4})-[:JUBILEE {time:4}]->(wp:Station:Zone4 {name:'Wembley Park', zone:4})
MERGE (wp)<-[:JUBILEE {time:4}]-(nae:Station:Zone3 {name:'Neasden', zone:3})
MERGE (nae)<-[:JUBILEE {time:2}]-(dh:Station:Zone3 {name:'Dollis Hill', zone:3})
MERGE (dh)-[:JUBILEE {time:2}]->(wg:Station:Zone1 {name:'Willesden Green', zone:1})
MERGE (wg)<-[:JUBILEE {time:2}]-(kil:Station:Zone1 {name:'Kilburn', zone:1})
MERGE (kil)-[:JUBILEE {time:2}]->(wh:Station:Zone1 {name:'West Hampstead', zone:1})
MERGE (wh)<-[:JUBILEE {time:1}]-(fr:Station:Zone1 {name:'Finchley Road', zone:1})
MERGE (fr)-[:METROPOLITAN {time:7}]->(wp)
RETURN count(*);