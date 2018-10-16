/*
 * File         : QueriesPerformer.java
 * Project      : Indexing and Search with Apache Lucene
 * Authors      : Hochet Guillaume 16 octobre 2018
 *                Guidoux Vincent 16 octobre 2018
 *
 * Description  : Class that define how the query are made and propose methods to
 * 					print top ranking terms
 *
 */
package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Comparator;

public class QueriesPerformer {
	
	private Analyzer		analyzer		= null;
	private IndexReader 	indexReader 	= null;
	private IndexSearcher 	indexSearcher 	= null;

	public QueriesPerformer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		Path path = FileSystems.getDefault().getPath("index");
		Directory dir;
		try {
			dir = FSDirectory.open(path);
			this.indexReader = DirectoryReader.open(dir);
			this.indexSearcher = new IndexSearcher(indexReader);
			if(similarity != null)
				this.indexSearcher.setSimilarity(similarity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printTopRankingTerms(String field, int numTerms) {

		Comparator comparator = new HighFreqTerms.DocFreqComparator();

		try {
			TermStats[] stats = HighFreqTerms.getHighFreqTerms(this.indexReader, numTerms, field, comparator);
			System.out.println("Top ranking terms for field [" + field + "] are: ");
			for(TermStats stat : stats)
				System.out.println(stat.termtext.utf8ToString() + " with " + stat.docFreq + " frequencies");
		} catch (Exception e) {
			System.out.println("Failed querying");
		}
	}
	
	public void query(String q) {

        try {
            QueryParser parser = new QueryParser("summary", this.analyzer);
            Query query = parser.parse(q);

            ScoreDoc[] results = indexSearcher.search(query, 5000).scoreDocs;

            System.out.println("Searching for [" + q + "]");
            System.out.println("Results found: " + results.length);

            int limit = results.length > 10 ? 10 : results.length;

            for (int i = 0; i < limit; i++) {
                ScoreDoc result = results[i];
                Document doc = indexSearcher.doc(result.doc);
                System.out.println(doc.get("id") + ":" + doc.get("title") + "(" + result.score + ")");
            }
        } catch (Exception e) {
            System.out.println("Query couldnt be performed, error: " + e.getMessage());
        }
	}
	 
	public void close() {
		if(this.indexReader != null)
			try { this.indexReader.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
	
}
