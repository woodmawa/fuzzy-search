package testScripts

import compare.service.CompareSentence

def cs = new CompareSentence()

def source = "the boots ltd"
def target = "boots, company plc"



if (source)
    cs.setSource(source)
if (target)
    cs.setTarget(target)

//println "source words list : $cs.sourceWords"
//println "target words list : $cs.targetWords"

//def result = cs.similarity()

//println "result : $result"

sentenceList = ["boots plc", "boots the chemist", "boots ltd."]

cs.setTargetList(sentenceList)
println "source is : $cs.source"
println "sentence list  is : $cs.targetList"

result = cs.similarityToList()
println "compare to sentence list  : \n"
result.each {println it}

