# Algoritmo Genético Paralelizável (AGP) 

O AGP implementado tem como função objetivo (_Fitness_) o coeficiente de Pearson. 
A combinação dos valores de um Indivíduo (I) com as _Métricas Computadas_ (MC) é correlacionada 
com as _Métricas Externas_ (ME) (ambas as métricas são disponibilizados em arquivos CSV).

O arquivo _Métricas Computadas_ (MC) é composto por um conjunto de tuplas de tamanho n, onde:

![img.png](imgs/Def-MC.png)

Cada indivíduo é composto por uma lista de **Pesos**. Cada Peso tem uma precisão de até 6 
casas decimais. A quantidade de **Pesos** de um indivíduo deve coincidir com o tamanho das tuplas
do arquivo _Métricas Computadas_ (MC).

A combinação do Indivíduo (I) com as _Métricas Computadas_ (MC) ocorrem da seguinte maneira:

![img.png](imgs/Combinação-IxMC.png)

### Atividades paralelizadas

O AGP utiliza recursos de paralelização através do uso de [_Executor Service_](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html)
do Java. Existem dois grupos de atividades que podem ser paralelizados nessa implementação, sendo eles:
_Cálculo do Fitness e Probabilidades_; _Seleção e Reprodução de Indivíduos_. 

Devido a concorrência no momento da inclusão de um novo _Indivídio_ na estrutura de dados
de **Lista dos Filhos**, foi necessário adicionar um mecanismo de exclusão mutua para evitar perda
de informação (**Synchronized**).

### Critério de parada

O único critério de parada definido nessa solução foi o número de gerações percorridas.

# Exemplo

No diretório raiz é possível encontrar uma pasta chamada _Sample_ composta pelas seguintes pastas:
* **input** - pasta de contém os arquivos _computed_metrics.csv_ (MC) e _external_metrics.csv_ (ME).
* **jar** - pasta que contém o arquivo _JAR_ do código compilado.
* **logs** - pasta com os _LOGS_ das execuções realizadas para a geração da documentação. Formato do nome dos arquivos:
```case-[GERACOES/100]-[POPULACAO/1000]-[T-FITNESS][T-SELECT]-0.log```
* **results** - pasta com o arquivo Excel com os resultados e gráficos consolidados.

# Linguagem utilizada, framework e versão

Para realizar o desenvolvimento inicial, foi utilizado Java 18 com Spring Boot Standalone (https://spring.io/projects/spring-boot) em sua versão 2.5.4.


# Recursos utilizados:

A seguir são listados os principais recursos adicionados utilizados no serviço:

1. [Lombok](https://projectlombok.org/) - biblioteca Java que cria automaticamente alguns recursos para manipular objetos;
2. [Open CSV](https://opencsv.sourceforge.net/) - biblioteca Java que auxilia na manipulação de arquivos CSV;
3. [Apache Commons Math 3](https://commons.apache.org/proper/commons-math/) - biblioteca Java que contém, entre outras coisas, a implementação da Correlação de Pearson;
4. [JUnit 5](https://junit.org/junit5/) - framework utilizado para realizar testes unitários.
5. [Mockito](https://site.mockito.org/) - estrutura de teste que permite a criação de objetos duplos de teste em testes de unidade automatizados;


# Testes

Foram realizados alguns testes unitários automatizados e testes de desenvolvimento.


# Registro de Log

Todos os erros que possam vir a acontecer na execução do AGP serão registrados (_Logados_). 
Também ocorre o registro das informações de execução:
- **Tempo Total de Execução**
- **Tempo Médio do Cálculo de Fitness**
- **Tempo Médio da Seleção/Reprodução**
- **Lista dos tempos Individuais de Fitness**
- **Lista dos tempos Individuais de Seleção/Reprodução**


# Build

A solução foi desenvolvido utilizando o [Gradle 7.5.1](https://gradle.org/).

Para construir o projeto o usuário deverá possuir o Gradle instalado no seu computador.

No terminal do sistema operacional, vá até à pasta raiz do projeto e execute o comando:

```shell
gradle wrapper
```

Após finalizar a execução anterior execute:

#### Windows: 
```shell 
.\gradlew.bat clean build
```
#### Linux: 
```shell
.\gradlew clean build
```

# Run

Para executar o AGP é necessário informar alguns parâmetros de entrada, são eles:

* **ga-parallel.input.computed-metrics** (_obrigatório_) - caminho para o arquivo de entrada Métricas Computadas (MC);
* **ga-parallel.input.external-metrics** (_obrigatório_) - caminho para o arquivo de entrada Métricas Externas (ME);
* **ga-parallel.population.size** (_opcional, default=10.000_) - Tamanho da população;
* **ga-parallel.population.generation** (_opcional, default=20_) - Número de gerações;
* **ga-parallel.fitness.executor.pool.number** (_opcional, default=2_) - Quantidade de _Threads_ que serão executadas durante a Função Fitness/Probabilidade;
* **ga-parallel.select.executor.pool.number** (_opcional, default=2_) - Quantidade de _Threads_ que serão executadas durante a Seleção/Reprodução;
* **ga-parallel.reproduction.mutation-rate** (_opcional, default=0.01_) - Taxa de mutação;
* **ga-parallel.input.weight.number** (_opcional, default=10_) - Número de pesos de cada Indivíduo;

#### Comando de Execução:

```shell
java -jar ./Sample/jar/genetic-algorithm-parallel-1.0.jar \ 
--ga-parallel.input.computed-metrics=./Sample/input/computed_metrics.csv \
--ga-parallel.input.external-metrics=./Sample/input/external_metrics.csv \ 
--ga-parallel.fitness.executor.pool.number=1 \ 
--ga-parallel.select.executor.pool.number=1 \ 
--ga-parallel.population.size=5000 \ 
--ga-parallel.population.generation=5
```

# Licença

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)

# Autor e Contato

- Autor: Vanderson Sampaio
- Email: vandersons.sampaio@gmail.com
- Github: https://github.com/vandersonsampaio
- Linkedin: https://www.linkedin.com/in/vanderson-sampaio-399973158/