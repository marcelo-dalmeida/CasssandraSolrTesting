package main;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class Main {

	public static void main(String[] args) {

		Cluster cluster;
		Session session;
	
		// Connect to the cluster and keyspace "demo"
	  	cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
	  	session = cluster.connect();
	    session.execute("CREATE KEYSPACE IF NOT EXISTS demo WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true");
	  	session = cluster.connect("demo");
	  	
	  	AnimalsDao animalsDao = new AnimalsDao(session);
	  	session.execute("CREATE TABLE IF NOT EXISTS animals(name text, type text, PRIMARY KEY(name));");
	  	
        //given
        String expectedName = "cat";
        String expectedType = "mammal";
        Animal newAnimal = new Animal(expectedName, expectedType);

        //when
        animalsDao.insert(newAnimal);

        //then
        ResultSet resultSet = session.execute("SELECT name, type FROM animals WHERE name = 'cat'");
        
        for (Row row : resultSet) {
			System.out.format("%s", row.getString("name"));
		}
		  	
	}	

}
