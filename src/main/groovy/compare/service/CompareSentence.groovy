package compare.service

import info.debatty.java.stringsimilarity.Levenshtein

class CompareSentence {

    String[] targetList = []
    private String target
    List targetWords = []
    private String source
    List sourceWords = []
    private StringTokenizer splitter
    private levenshtein = new Levenshtein()

    //convert source and target into uppercase to compare
    def setTarget (t) {

        def word
        targetWords = [] //reset target words

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

    def setTargetList (tl) {

        def word

        assert tl instanceof ArrayList<String>

        this.targetList = tl.collect {it.toUpperCase()}
    }

    def setSource (s) {
        def word
        sourceWords = [] //reset

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
        double totalVariance = 0.0

        results << [words:max]
        def shorterWords = (source.size() <= target.size()) ? sourceWords : targetWords
        def longerWords = shorterWords.is(sourceWords) ? targetWords : sourceWords

        for (int i = 0; i < max ; i++) {
            def dist
            double varComparedToSize

            def cword = (i >= Math.min(sSize,tSize)) ? "" : shorterWords[i]
            dist = levenshtein.distance(longerWords[i], cword  )
            varComparedToSize = dist/cword.size()
            totalVariance += varComparedToSize
            results  << [dist:dist]  << [sVar:varComparedToSize]

        }
        results << [averageVar:(totalVariance/max)]  //not a good measure !
    }

    def similarityToList () {
        def results = []

        results =  targetList.collect {target = it;   similarity()}
        results
    }
}
