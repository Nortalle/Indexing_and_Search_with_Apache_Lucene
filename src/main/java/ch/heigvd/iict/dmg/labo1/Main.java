/*
 * File         : Main.java
 * Project      : Indexing and Search with Apache Lucene
 * Authors      : Hochet Guillaume 16 octobre 2018
 *                Guidoux Vincent 16 octobre 2018
 *
 * Description  : The goal of this lab is to discover the Lucene platform and to learn its
 * functionalities by using its Java API. Lucene is a library for indexing and
 * searching text files, written in Java and available as open source under the
 * Apache License. It is not a standalone application; it is designed to be
 * integrated easily into applications that have to search text in local files or
 * on the Internet. It attempts to balance efficiency, flexibility, and
 * conceptual simplicity at the API level.
 *
 */
package ch.heigvd.iict.dmg.labo1;

import ch.heigvd.iict.dmg.labo1.indexer.CACMIndexer;
import ch.heigvd.iict.dmg.labo1.parsers.CACMParser;
import ch.heigvd.iict.dmg.labo1.queries.QueriesPerformer;
import ch.heigvd.iict.dmg.labo1.similarities.MySimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // 1.1. create an analyzer
        Analyzer analyser = getAnalyzer();

        // TODO student "Tuning the Lucene Score"
        Similarity similarity = new ClassicSimilarity();
        //Similarity similarity = new MySimilarity();

        CACMIndexer indexer = new CACMIndexer(analyser, similarity);
        long time = System.currentTimeMillis();
        indexer.openIndex();
        CACMParser parser = new CACMParser("documents/cacm.txt", indexer);
        parser.startParsing();
        indexer.finalizeIndex();
        System.out.println("Time taken: " + (System.currentTimeMillis() - time));

        QueriesPerformer queriesPerformer = new QueriesPerformer(analyser, similarity);

        // Section "Reading Index"
        readingIndex(queriesPerformer);

        // Section "Searching"
        searching(queriesPerformer);

        queriesPerformer.close();

    }

    private static void readingIndex(QueriesPerformer queriesPerformer) {
/*
		queriesPerformer.printTopRankingTerms("authors", 10);
		queriesPerformer.printTopRankingTerms("title", 10);
*/
    }

    private static void searching(QueriesPerformer queriesPerformer) {
		queriesPerformer.query("compiler program");
		/*
		queriesPerformer.query("Information Retrieval");
		queriesPerformer.query("Information AND Retrieval");
		queriesPerformer.query("Retrieval AND Information~ -Database");
		queriesPerformer.query("Info*");
		queriesPerformer.query("Information Retrieval~5");*/
    }

    private static Analyzer getAnalyzer() {

        //return new StandardAnalyzer();
        //return new WhitespaceAnalyzer();
        return new EnglishAnalyzer();
        //return new ShingleAnalyzerWrapper(3,3);
		/*
        try {
            FileReader reader = new FileReader("common_words.txt");
            return new StopAnalyzer(reader);
        } catch (IOException e) {
            System.out.println("FAIL MAMENE");
            return null;
        }
        */
    }

}
