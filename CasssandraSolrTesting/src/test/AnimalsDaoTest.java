package test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import main.Animal;
import main.AnimalsDao;

public class AnimalsDaoTest {

    @Rule
    public CassandraCQLUnit cassandraCQLUnit = new CassandraCQLUnit(new ClassPathCQLDataSet("animals.cql"), EmbeddedCassandraServerHelper.DEFAULT_CASSANDRA_YML_FILE, (long) 30000);
    
    private AnimalsDao animalsDao;

    @Before
    public void setUp() throws Exception {
        animalsDao = new AnimalsDao(cassandraCQLUnit.session);
    }
    
    @Test
    public void testReturnAllAnimals() throws Exception {
        //given
        Animal expectedEagle = new Animal("eagle", "bird");
        Animal expectedClownFish = new Animal("clown fish", "fish");
        Animal expectedReindeer = new Animal("reindeer", "mammal");
        Animal expectedFrog = new Animal("frog", "amphibian");

        //when
        List<Animal> animalsList = animalsDao.find();

        //then
        assertThat(animalsList, hasItem(expectedEagle));
        assertThat(animalsList, hasItem(expectedClownFish));
        assertThat(animalsList, hasItem(expectedReindeer));
        assertThat(animalsList, hasItem(expectedFrog));
    }

    @Test
    public void testInsertsNewAnimal() throws Exception {
        //given
        String expectedName = "cat";
        String expectedType = "mammal";
        Animal newAnimal = new Animal(expectedName, expectedType);

        //when
        animalsDao.insert(newAnimal);

        //then
        ResultSet resultSet = cassandraCQLUnit.session.execute("SELECT name, type FROM animals WHERE name = 'cat'");

        List<Row> rows = resultSet.all();
        assertThat(rows.size(), equalTo(1));

        Row row = rows.get(0);
        assertThat(row.getString("name"), equalTo(expectedName));
        assertThat(row.getString("type"), equalTo(expectedType));
    }
}