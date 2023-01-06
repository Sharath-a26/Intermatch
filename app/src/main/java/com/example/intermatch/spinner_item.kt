package com.example.intermatch

class spinner_item {
    lateinit var item_name : String
    constructor(
        name : String
    ) {
        this.item_name = name
    }

    fun getSpinnerItemName() : String {
        return this.item_name
    }
}