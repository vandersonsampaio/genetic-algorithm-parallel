package br.ufsc.ine.ppgcc.debug;

/**
 * Entidade auxiliar responsavel por registrar tempos de início e fim de alguma execução
 */
public class Info {

    private final long startTime;
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
}
