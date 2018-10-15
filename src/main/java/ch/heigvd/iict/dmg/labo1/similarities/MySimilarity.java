package ch.heigvd.iict.dmg.labo1.similarities;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MySimilarity extends ClassicSimilarity {

    @Override
    public float lengthNorm(FieldInvertState state) {
        return 1;
    }

    @Override
    public float tf(float freq) {

        return (float)Math.log(freq) + 1;
    }

    @Override
    public float idf(long docFreq, long docCount) {

        return (float)(Math.log((docCount / docFreq + 1) + 1));
    }

    @Override
    public float coord(int overlap, int maxOverlap) {

        return (float)Math.sqrt(overlap / maxOverlap);
    }
}
