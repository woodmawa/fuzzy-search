package util

println "get start of epoch in milliseconds " + UuidUtil.getStartOfUuidRelativeToUnixEpochInMilliseconds()
assert UuidUtil.START_OF_UUID_RELATIVE_TO_UNIX_EPOCH_MILLIS == UuidUtil.startOfUuidRelativeToUnixEpochInMilliseconds

UUID tuid = UuidUtil.timeBasedUuid

println "uid : $tuid"

Date date = UuidUtil.getDateFromUuid(tuid)
println "extracted date from uid is " + UuidUtil.getDateFromUuid(tuid)
