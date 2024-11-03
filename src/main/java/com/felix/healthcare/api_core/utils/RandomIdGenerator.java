package com.felix.healthcare.api_core.utils;

import java.util.Random;

public class RandomIdGenerator {

    public static String randomGenerator(String prefix){
        Random random = new Random();

        // Generates a random number between 100 and 999 and add 'Task' as prefix
        int randomNumber = 100 + random.nextInt(900);
        return prefix + randomNumber;
    }

}
