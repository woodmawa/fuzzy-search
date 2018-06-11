package compare.service

import info.debatty.java.stringsimilarity.Levenshtein

class CompareSentence {

    private String target
    List targetWords = []
    private String source
    List sourceWords = []
    private StringTokenizer splitter
    private levenshtein = new Levenshtein()

    //convert source and target into uppercase to compare
    def setTarget (t) {

        def word
        if (t.class != String & t.class != GString )
            throw new RuntimeException()

        this.target = t.toUpperCase()
        splitter = new StringTokenizer(target, " ,")
        while (splitter.hasMoreElements()) {
            word = splitter.nextElement().toString()
            switch (word) {
                case "the".toUpperCase():
                case "a".toUpperCase():
                    break
                default: targetWords << word
            }
        }
    }

    def setSource (s) {
        def word
        if (s.class != String & s.class != GString )
            throw new RuntimeException()

        this.source = s.toUpperCase()
        splitter = new StringTokenizer(source, " ,")
        while (splitter.hasMoreElements()) {
            word = splitter.nextElement().toString()
            switch (word) {
                case "the".toUpperCase():
                case "a".toUpperCase():
                    break
                default: sourceWords << word
            }
        }
    }

    //do simple compare first word by word
    def similarity () {


        def results =  []

        def sSize = sourceWords.size()
        def tSize = targetWords.size()
        def max = Math.max(sSize,tSize)

        results << max
        def shorterWords = (source.size() <= target.size()) ? sourceWords : targetWords
        def longerWords = shorterWords.is(sourceWords) ? targetWords : sourceWords

        for (int i = 0; i < max ; i++) {

            def cword = (i >= Math.min(sSize,tSize)) ? "" : shorterWords[i]
            results << levenshtein.distance(longerWords[i], cword  )

        }
        results
    }
}
