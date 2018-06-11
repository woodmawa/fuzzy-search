package testScripts

import compare.service.CompareSentence

def cs = new CompareSentence()

def source = "the boots"
def target = "boots, company plc"



if (source)
    cs.setSource(source)
if (target)
    cs.setTarget(target)

println "source words list : $cs.sourceWords"
println "target words list : $cs.targetWords"

def result = cs.similarity()

println "result : $result"