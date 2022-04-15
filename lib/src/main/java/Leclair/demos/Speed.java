package Leclair.demos;

import Leclair.application.ApplicationStructure;

public class Speed extends ApplicationStructure {

    public static void main(String[] args) {
        Speed s = new Speed();
        s.start();
    }

    @Override
    public void appSetup() {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");
    }

    @Override
    public void appLoop() {
   
    }

    @Override
    public void appCleanup() {
    
    }
    
}
