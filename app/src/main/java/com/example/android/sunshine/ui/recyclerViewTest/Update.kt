package com.example.android.sunshine.ui.recyclerViewTest

abstract class Update(
//        val _id: Int,
        val _type: String
//        val _date: Date
) {

    class TYPE {
        companion object {
            const val WEATHER = "weather"
            const val DETAILS = "details"
            const val GRAPH = "graph"
            const val MAP = "map"
        }
    }

}