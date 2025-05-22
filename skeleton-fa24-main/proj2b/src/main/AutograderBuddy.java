package main;

import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.graphBuilder;
import ngrams.wordGraph;


public class AutograderBuddy {
    /** Returns a HyponymHandler */
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordFile, String countFile,
            String synsetFile, String hyponymFile) {

        NGramMap ngm=new NGramMap(wordFile,countFile);

        graphBuilder builder=new graphBuilder(synsetFile,hyponymFile);
        wordGraph wg=builder.getWg();

        return new HyponymsHandler(wg,ngm);
    }
}
