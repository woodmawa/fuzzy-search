import info.debatty.java.stringsimilarity.Cosine
import info.debatty.java.stringsimilarity.Damerau
import info.debatty.java.stringsimilarity.Levenshtein
import info.debatty.java.stringsimilarity.NGram
import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import info.debatty.java.stringsimilarity.QGram
import info.debatty.java.stringsimilarity.experimental.Sift4

def companies = [
        "Boots",
        "boots",
        "Boots UK",
        "Boots Ltd",
        "Boots the Chemist PLC"

]

def s1 = "Boots"

def fcompare = new Damerau();

//do uppercase compare using various methods
def fuzzy (fnc, String s1, list) {
    list.collect {fnc.distance (s1.toUpperCase(), it.toUpperCase())}
}

def res = fuzzy (fcompare, s1, companies)
println "damereau : $res"

fcompare = new Sift4 ()
fcompare.setMaxOffset(5)
res = fuzzy (fcompare, s1, companies)
println "sift4 : $res"

fcompare = new NormalizedLevenshtein()
res = fuzzy (fcompare, s1, companies)
println "normalised lev : $res"

fcompare = new Levenshtein()
res = fuzzy (fcompare, s1, companies)
println "levenstein : $res"


fcompare = new Cosine()
res = fuzzy (fcompare, s1, companies)
println "cosine : $res"

fcompare = new QGram(5)

res = fuzzy (fcompare, s1, companies)
println "qgram : $res"

fcompare = new NGram(4)
res = fuzzy (fcompare, s1, companies)
println "ngram : $res"

