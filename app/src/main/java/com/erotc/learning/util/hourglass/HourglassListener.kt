package com.erotc.learning.util.hourglass

/**
 * Created by Ankush Grover(ankush.dev2@gmail.com) on 28/12/17.
 */
interface HourglassListener {
    /**
     * Method to be called every second by the [Hourglass]
     *
     * @param timeRemaining: Time remaining in milliseconds.
     */
    fun onTimerTick(timeRemaining: Long)

    /**
     * Method to be called by [Hourglass] when the thread is getting  finished
     */
    fun onTimerFinish()
}