package com.dgo.alarm.utilities;

/**
 * Created by david.gonzalez on 28/07/2015.
 */

import android.util.Log;

/**
 * pseudorandom number generator
 */
public class Prng {
    private long a, c, m;
    private long x;

    public Prng(long a, long c, long m, long seed){
        x = seed;
        this.a = a;
        this.c = c;
        this.m = m;
    }
    public Prng(long seed, long maxNumber){
        this(137,187,maxNumber,seed);
    }

    public long rand(){
        x = (a*x + c)%m;
        Log.e("COUCOU", "Value : " + x);
        return x;
    }
}
