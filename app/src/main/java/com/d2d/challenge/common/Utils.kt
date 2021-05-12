package com.d2d.challenge.common


/**
 * An inline higher order function to check pair of nullable values.
 * @param a first nullable parameter
 * @param b second nullable parameter
 * @param action is a lambda expression take function as a parameter
 */
inline fun <A, B, R> ifLatLngNotNull(a: A?, b: B?, action: (A, B) -> R) {
    if (a != null && b != null) {
        action(a, b)
    }
}


/**
 * Used for trim double quotes from the beginning and ending of a string
 * @param text string to be trimmed
 * @return returns trimmed string
 */
fun trimDoubleQuotes(text : String): String = text.replace("^\"+|\"+\$".toRegex(), "")



/**
 * Delay UI thread for a given time
 * @param i milliseconds in Long
 */
fun delay(i: Long) = Thread.sleep(i)