package test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.util.AbstractSolrTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolrSampleTest extends AbstractSolrTestCase {

    private EmbeddedSolrServer server;
    private CoreContainer container;

    
    @Before
    @Override
    public void setUp() throws Exception {
      // If indexing a brand-new index, you might want to delete the data directory first
      // FileUtilities.deleteDirectory("testdata/solr/collection1/data");

      container = new CoreContainer("testdata/solr");
      container.load();

      server = new EmbeddedSolrServer(container, "collection1" );
      super.setUp();
    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException, IOException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(0, response.getResults().getNumFound());
    }

    @Test
    public void testThatDocumentIsFound() throws SolrServerException, IOException 
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1");
        document.addField("name", "my name");
        server.add(document);
        server.commit();

        SolrParams params = new SolrQuery("name");
        QueryResponse queryResponse = server.query(params);
        
        assertEquals(1, queryResponse.getResults().getNumFound());
        assertEquals("1", queryResponse.getResults().get(0).get("id"));
    }
    
    
    @Test
    public void testThatDocumentIsFoundWithModifiableSolrParams() throws SolrServerException, IOException 
    {
		ModifiableSolrParams params = new ModifiableSolrParams();
		
		// ** Let's index a document into our embedded server
		
		SolrInputDocument newDocument = new SolrInputDocument();
		newDocument.addField("title", "Test Document 1");
		newDocument.addField("id", "doc-1");
		newDocument.addField("text", "Hello world!");
		server.add(newDocument);
		server.commit();
	   
		// ** And now let's query for it

		params.set("q", "test");
		QueryResponse queryResponse = server.query(params);
		
		assertEquals(1, queryResponse.getResults().getNumFound());
        assertEquals("doc-1", queryResponse.getResults().get(0).get("id"));
    }
    
    
    @After
    public void tearDown() throws Exception {
	   server.close();
	   super.tearDown();
	}
}