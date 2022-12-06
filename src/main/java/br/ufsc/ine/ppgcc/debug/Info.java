package br.ufsc.ine.ppgcc.debug;

public class Info {

    private long startTime;
    private long finalTime;

    public Info() {
        startTime = System.currentTimeMillis();
    }

    public void finishCount() {
        finalTime = System.currentTimeMillis();
    }

    public long totalTime() {
        return finalTime - startTime;
    }

    public void resetStartTime() {
        startTime = System.currentTimeMillis();
    }
}
