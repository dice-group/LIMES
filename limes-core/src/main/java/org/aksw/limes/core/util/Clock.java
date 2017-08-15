/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.aksw.limes.core.util;

/**
 *
 * @author ngonga
 */
public class Clock {
    long begin;
    long lastClick;
    public Clock()
    {
        begin = System.currentTimeMillis();
        lastClick = begin;
    }

    public long durationSinceClick()
    {
        long help = lastClick;
        lastClick = System.currentTimeMillis();
        return lastClick - help;
    }

    public long totalDuration()
    {
        lastClick = System.currentTimeMillis();
        return lastClick - begin;
    }
}
