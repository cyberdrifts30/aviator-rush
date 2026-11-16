package com.Qempibo9.Pebfik.domain.model

enum class Rank(val requiredMiles: Int) {
    CADET(0),
    PILOT(1000),
    CAPTAIN(5000),
    ACE(15000),
    LEGEND(40000)
}
