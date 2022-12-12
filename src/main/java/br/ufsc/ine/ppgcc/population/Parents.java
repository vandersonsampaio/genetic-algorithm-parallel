package br.ufsc.ine.ppgcc.population;

import lombok.AllArgsConstructor;

/**
 * Entidade composta por dois indivíduos, o Pai Um e Pai Dois
 */
@AllArgsConstructor
public class Parents {
    private Individual parentOne;
    private Individual parentTwo;

    /**
     * Método auxiliar de busca dos Pais
     * @param one se o valor buscado é o pai um
     * @return caso o parâmetro de entrada seja verdadeiro ele retorna o Pai Um, senão o Pai Dois
     */
    public Individual getParent(boolean one) {
        return one ? parentOne : parentTwo;
    }
}
