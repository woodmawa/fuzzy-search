import info.debatty.java.stringsimilarity.Cosine
import info.debatty.java.stringsimilarity.Damerau
import info.debatty.java.stringsimilarity.Levenshtein
import info.debatty.java.stringsimilarity.NGram
import info.debatty.java.stringsimilarity.NormalizedLevenshtein
import info.debatty.java.stringsimilarity.QGram
import info.debatty.java.stringsimilarity.experimental.Sift4
import me.xdrop.fuzzywuzzy.FuzzySearch

String s1 = "Adobe CreativeSuite 5 Master Collection from cheap 4zp"
String s2 = "Adobe CreativeSuite 5 Master Collection from cheap d1xx"

def res = FuzzySearch.ratio ("mybasestring", "mySimilarString")
println "ratio: $res"

res = FuzzySearch.weightedRatio("boots the chemist", "boots plc")
println "weighted ratio for boots: $res"

Damerau d = new Damerau();

//minimum number of chnages to get from one string to another
res = d.distance("hi there", "hih there plc")
println "damerou distance : $res"

Levenshtein lev = new Levenshtein()
NormalizedLevenshtein normlev = new NormalizedLevenshtein()

res = lev.distance("hi there", "hih there plc")
println "lev dist distance : $res"

res = normlev.distance ("hi there", "hih there plc")
println "norm lev dist distance : $res"

Sift4 sift4 = new Sift4();
sift4.setMaxOffset(4);

res = sift4.distance("a tub of lard", "tub a of lard")
println "sift4 distance : $res"

Cosine cos = new Cosine()
res = cos.distance("a tub of lard", "tub a of lard")
println "cosine distance : $res"

QGram g = new QGram(5)
res = g.distance("a tub of lard", "ah tub of lard")
println "qgram distance : $res"

NGram twogram = new NGram(2);
println("ngram(2) disctance : " + twogram.distance("ABCD", "ABTUIO"))

// produces 0.97222

NGram ngram = new NGram(4);
println("ngram(4) distance" + ngram.distance(s1, s2))

res = sift4.distance(s1, s2)
println "sift4 distance long string : $res"

sift4.distance "boots the chemist", "boots plc"
println "sift4 distance for boots : $res"
