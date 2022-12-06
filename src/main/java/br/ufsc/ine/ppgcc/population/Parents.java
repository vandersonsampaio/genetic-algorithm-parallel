package br.ufsc.ine.ppgcc.population;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Parents {
    private Individual parentOne;
    private Individual parentTwo;

    public Individual getParent(boolean one) {
        return one ? parentOne : parentTwo;
    }
}
