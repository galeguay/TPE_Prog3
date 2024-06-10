package tpe;

import tpe.model.Procesador;
import tpe.model.Solucion;
import tpe.model.Tarea;
import tpe.model.Arbol;
import tpe.utils.CSVReader;

import java.util.*;

/**
 * NO modificar la interfaz de esta clase ni sus métodos públicos.
 * Sólo se podrá adaptar el nombre de la clase "Tarea" según sus decisiones
 * de implementación.
 */
public class Servicios {

    private HashMap<String, Tarea> tareas = new HashMap<>();
    private HashMap<String, Procesador> procesadores = new HashMap<>();
    private Arbol arbolTareas = new Arbol();

    //PARA ETAPA 2
    Integer LIMITE_PROCESADOR_NO_R;
    private Solucion mejorSolucion = new Solucion();


    /* Complejidad temporal O(n):
        En el constructor el metodo readProcessors() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Procesadores.csv.
        El metodo siguiente readTasks() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Tareas.csv. Teniendo en cuenta
        que dentro del metodo se hace una insercion a un arbol que es O(h), suponiendo que es un arbol balanceado. Y tambien se hace una
         insercion a un HashMap que es O(1). Ya que se supone el peor caso, como conclusion la Complejidad temporal O(n). */
    public Servicios(String pathProcesadores, String pathTareas) {
        CSVReader reader = new CSVReader();
        reader.readProcessors(pathProcesadores, procesadores);
        reader.readTasks(pathTareas, arbolTareas, tareas);
    }

    /* Complejidad temporal O(1):
        En el servicio1 es O(1) ya que una busqueda en un HashMap siempre es O(1). */
    public Tarea servicio1(String ID) {
        return tareas.get(ID);
    }

    /* Complejidad temporal O(n):
        En el servicio2 es O(n) ya que tiene que comprobar todas las tareas, sean criticas o no.*/
    public List<Tarea> servicio2(boolean esCritica) {
        List<Tarea> lista = new ArrayList<>();
        for (String IDtarea : tareas.keySet()) {
            if (esCritica) {
                if (tareas.get(IDtarea).isCritica())
                    lista.add(tareas.get(IDtarea));
            } else {
                if (!tareas.get(IDtarea).isCritica())
                    lista.add(tareas.get(IDtarea));
            }
        }
        return lista;
    }


    /* Complejidad temporal O(n):
		En el servicio3 es O(n) ya que en el peor de los casos se puede solicitar el rango 1-100 que abarca todas las tareas.*/
    public List<Tarea> servicio3(int prioridadInferior, int prioridadSuperior) {
        return arbolTareas.enlistarRangoPrioridad(prioridadInferior, prioridadSuperior);
    }

    /** (Resolución de etapa 2: Backtracking). Asigna las tareas usando Backtracking.
     * COMPLEJIDAD: O(p^t) -> siendo p:procesadores y t:tareas
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    public Solucion asignarTareasBacktracking(int tiempoLimitePNoRefrigerado) {
        LIMITE_PROCESADOR_NO_R = tiempoLimitePNoRefrigerado;
        mejorSolucion.setTiempoSolucion(Integer.MAX_VALUE);

        //preparar hash solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
        }

        // Convertir el HashMap a ArrayList
        List<Tarea> listaTareas = new ArrayList<>(tareas.values());
        backtracking(0, listaTareas, solucionParcial);
        return mejorSolucion;
    }


    /**Metodo recursivo que ejecuta el algoritmo Backtracking para obtener la asignación de tareas tal que el tiempo de ejecución sea el mínimo
     * Estrategia:
     *
     * @param tareaIndex
     * @param listaTareas
     * @param solucionParcial
     */
    public void backtracking(int tareaIndex, List<Tarea> listaTareas, HashMap<String, ArrayList<Tarea>> solucionParcial) {
        mejorSolucion.sumarMetrica();

        // Si hemos asignado todas las tareas, evaluamos la solución
        if (tareaIndex == listaTareas.size()) {
            Integer tiempoSolucionActual = max(solucionParcial);

            if (tiempoSolucionActual < mejorSolucion.getTiempoSolucion()) {
                mejorSolucion.setTiempoSolucion(tiempoSolucionActual);
                reemplazarMejorSolucion(solucionParcial);
            }
            return;
        }

        // Obtenemos la tarea actual a asignar
        Tarea tareaActual = listaTareas.get(tareaIndex);

        //El siguiente array se usa para el chequear aquellas tareas que no puedan ser asignadas en ninguno de los procesadores por las condiciones de consigna
        boolean[] asignada = new boolean[solucionParcial.size()];
        int indexAsignada = 0;
        for (String procesador : procesadores.keySet()) {

            //CONDICIÓN 1: chequear si hay 2 criticas ya en la lista del procesador actual
            if (!tareaActual.isCritica() || !limiteCriticas(solucionParcial.get(procesador))) {
                //CONDICIÓN 2: si el procesador no es refrigerado, el tiempo de la tarea no debe superar el limite máximo ingresado
                if (procesadores.get(procesador).isRefrigerado() || (tareaActual.getTiempoDeEjecucion() < LIMITE_PROCESADOR_NO_R)) {

                    // Agregar tarea actual al procesador actual
                    solucionParcial.get(procesador).add(tareaActual);
                    asignada[indexAsignada]=true;

                    //PODA: si el tiempo maximo parcial supera al tiempo de la mejor solucion se poda, ya que no tiene sentido recorrer el resto.
                    if (max(solucionParcial) < mejorSolucion.getTiempoSolucion()) {
                        backtracking(tareaIndex + 1, listaTareas, solucionParcial);
                    }

                    // Quitar tarea actual del procesador actual
                    solucionParcial.get(procesador).remove(solucionParcial.get(procesador).size() - 1);
                }
            }
            indexAsignada++;
        }

        //chequeo de tareas NO asignadas
        boolean anyTrue = false;
        for (boolean b : asignada) {
            if (b) {
                anyTrue = true;
                break;
            }
        }
        if (!anyTrue){
            backtracking(tareaIndex + 1, listaTareas, solucionParcial);
            mejorSolucion.setTareaNoAsignada(tareaActual);
        }
    }

    /**Obtiene el tiempo de la suma de tareas, del procesador con mayor carga.
     * COMPLEJIDAD: O(p*t)
     * @param solucionParcial
     * @return
     */
    private Integer max(HashMap<String, ArrayList<Tarea>> solucionParcial) {
        Integer maxCarga = 0;
        for (String procesador : solucionParcial.keySet()) {
            Integer suma = 0;
            for (Tarea t : solucionParcial.get(procesador)) {
                suma += t.getTiempoDeEjecucion();
            }
            if (suma > maxCarga) {
                maxCarga = suma;
            }
        }
        return maxCarga;
    }

    /**
     * Chequea en el lista de tareas ya asignadas al procesador pasada por parametro, si hay dos tareas crítica
     *
     * @param tareasDelProcesador
     * @return
     */
    //COMPLEJIDAD: O(t)
    private boolean limiteCriticas(ArrayList<Tarea> tareasDelProcesador) {
        int contador = 0;
        for (Tarea t : tareasDelProcesador) {
            if (t.isCritica())
                contador++;
        }
        return contador >= 2;
    }

    /** Guarda la solucion parcial pasado por parámetro en la mejor solución. Lo hace recorriendo el HashMap de solucion parciol, y las listas de tareas asignada a cada procesador
     * COMPLEJIDAD: O(p*t)
     * @param solucionParcial
     */
    private void reemplazarMejorSolucion(HashMap<String, ArrayList<Tarea>> solucionParcial) {
        mejorSolucion.getHashSolucion().clear();

        // Copiar la solución parcial en la mejor solución
        for (Map.Entry<String, ArrayList<Tarea>> entry : solucionParcial.entrySet()) {
            String procesadorId = entry.getKey();
            ArrayList<Tarea> tareasProcesador = new ArrayList<>(entry.getValue()); // Copia profunda de las tareas

            mejorSolucion.getHashSolucion().put(procesadorId, tareasProcesador);
        }
    }

    /** (Resolución de etapa 2: Greedy). Asigna las tareas usando Greedy.
     * Estrategia:
     * 
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    //COMPLEJIDAD: O(P+(T*logT)+(P+T))
    public Solucion asignarTareasGreedy(int tiempoLimitePNoRefrigerado) {
        Solucion solucion = new Solucion();
        solucion.setNombreAlgoritmo("Greedy");

        HashMap<String, Integer> cargaProcesadores = new HashMap<>();
        ArrayList tareasNoAsignadas = new ArrayList();
        int considerados = 0;

        //COMPLEJIDAD: O(P)
        //preparar hash solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
            cargaProcesadores.put(p, 0);
        }

        //ordena lista de tareas
        List<Tarea> tareasOrdenadas = new ArrayList<>(tareas.values());

        //COMPLEJIDAD: O(T*logT)
        tareasOrdenadas.sort((t1, t2) -> Integer.compare(t2.getTiempoDeEjecucion(), t1.getTiempoDeEjecucion()));

        Iterator<Tarea> itTareas = tareasOrdenadas.iterator();

        //COMPLEJIDAD: O(P+(P+T)) -> O(P+T)
        while (itTareas.hasNext()) {
            Tarea tareaActual = itTareas.next();
            considerados += solucionParcial.size();

            //cargamos, en principio, todos los procesadores como posibles soluciones
            ArrayList<String> procesadoresRestantes = new ArrayList<>();

            //COMPLEJIDAD: O(P)
            for (String p : procesadores.keySet()) {
                procesadoresRestantes.add(p);
            }
            boolean asignada = false;

            //COMPLEJIDAD: O(P+T)
            while (!asignada && !procesadoresRestantes.isEmpty()) {
                //COMPLEJIDAD: O(P)
                String procesadorMenorCarga = menorCarga(cargaProcesadores, procesadoresRestantes);
                //COMPLEJIDAD: O(T)
                if (esFactible(solucionParcial, tareaActual, procesadorMenorCarga, tiempoLimitePNoRefrigerado)) {
                    solucionParcial.get(procesadorMenorCarga).add(tareaActual);
                    cargaProcesadores.put(procesadorMenorCarga, cargaProcesadores.get(procesadorMenorCarga) + tareaActual.getTiempoDeEjecucion());
                    asignada = true;
                } else {
                    //descartamos el procesador como solucion ya que no es factible, para la tarea actual
                    //COMPLEJIDAD: O(P)
                    procesadoresRestantes.remove(procesadorMenorCarga);
                    considerados--;
                }
            }
            if (!asignada && procesadoresRestantes.isEmpty()) {
                tareasNoAsignadas.add(tareaActual);
            }
        }

        solucion.setSolucion(solucionParcial);
        solucion.setMetrica(considerados);
        solucion.setTareasNoAsignadas(tareasNoAsignadas);
        return solucion;
    }

    /**Analiza si es viable agregar la tarea actual en el procesador actual, basandose en las siguientes restricciones:
     * Ningún procesador podrá ejecutar más de 2 tareas crítica.
     * Los procesadores no refrigerados no podrán dedicar más de X tiempo de ejecución a
     * las tareas asignadas
     * COMPLEJIDAD: O(T)
     * @param solucionParcial
     * @param tareaActual
     * @param procesador
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    private boolean esFactible(HashMap<String, ArrayList<Tarea>> solucionParcial, Tarea tareaActual, String procesador, int tiempoLimitePNoRefrigerado) {

        //CONDICIÓN 1: chequear si hay 2 criticas ya en la lista del procesador actual
        if (!tareaActual.isCritica() || !limiteCriticas(solucionParcial.get(procesador))) {

            //CONDICIÓN 2: si el procesador no es refrigerado, el tiempo de la tarea no debe superar el limite máximo ingresado
            if (procesadores.get(procesador).isRefrigerado() || (tareaActual.getTiempoDeEjecucion() < tiempoLimitePNoRefrigerado)) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Devuelve el ID del procesador con menor carga, de entre los considerados que contiene la lista pasada por parametros
     * COMPLEJIDAD: O(P)
     * @param cargaProcesadores
     * @param procesadoresRestantes
     * @return
     */
    private String menorCarga(HashMap<String, Integer> cargaProcesadores, List<String> procesadoresRestantes) {
        String idMenor = "";
        int menorCarga = Integer.MAX_VALUE;
        for (String procesador : cargaProcesadores.keySet()) {
            if (procesadoresRestantes.contains(procesador)) {
                int cargaActual = cargaProcesadores.get(procesador);
                if (cargaActual < menorCarga) {
                    menorCarga = cargaActual;
                    idMenor = procesador;
                }
            }
        }
        return idMenor;
    }

}