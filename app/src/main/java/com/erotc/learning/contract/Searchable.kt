package com.erotc.learning.contract

/**
 * Created on 5/27/2020.
 */
interface Searchable {
    fun isSearchEmpty(): Boolean
    fun clearInputSearch()
    fun showAll()
}