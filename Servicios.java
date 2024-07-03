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
    private List<Tarea> tareasCriticas = new ArrayList<>();
    private List<Tarea> tareasNoCriticas = new ArrayList<>();

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
        reader.readTasks(pathTareas, arbolTareas, tareas,tareasCriticas,tareasNoCriticas);
    }

    /* Complejidad temporal O(1):
        En el servicio1 es O(1) ya que una busqueda en un HashMap siempre es O(1). */
    public Tarea servicio1(String ID) {
        return tareas.get(ID);
    }

    /* Complejidad temporal O(1):
        En el servicio2 es O(1) ya que solo devuelve la lista de tareas criticas o no criticas.*/
    public List<Tarea> servicio2(boolean esCritica) {
        if (esCritica) {
            return this.tareasCriticas;
        }else{
            return this.tareasNoCriticas;
        }
    }

    /* Complejidad temporal O(n):
		En el servicio3 es O(n) ya que en el peor de los casos se puede solicitar el rango 1-100 que abarca todas las tareas.*/
    public List<Tarea> servicio3(int prioridadInferior, int prioridadSuperior) {
        return arbolTareas.enlistarRangoPrioridad(prioridadInferior, prioridadSuperior);
    }


    // ----------------- ETAPA 2 ----------------


    /**
     * (Resolución de etapa 2: Backtracking). Asigna las tareas usando Backtracking.
     * COMPLEJIDAD: O(p^t) -> siendo p:procesadores y t:tareas
     * **Estrategia**: En el algoritmo se utilizó la estructura HashMap para almacenar la solución parcial y la carga de los
     * procesadores, ya que HashMap permite búsquedas y actualizaciones en tiempo constante O(1), optimizando la eficiencia
     * del algoritmo.
     * Como se solicitaba, se aplicaron restricciones específicas:
     * El límite de 2 tareas críticas por procesador y el tiempo máximo de ejecución permitido para procesadores no refrigerados.
     * Como estrategia de poda, se verifica que el tiempo de ejecución de la solución parcial no supere el tiempo de la mejor
     * solución encontrada hasta el momento, descartando aquellas combinaciones que no pueden mejorar el resultado actual.
     * Esto permite reducir el espacio de búsqueda y acelerar el proceso de encontrar la mejor solucion posible.
     *
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    public Solucion asignarTareasBacktracking(int tiempoLimitePNoRefrigerado) {
        LIMITE_PROCESADOR_NO_R = tiempoLimitePNoRefrigerado;
        mejorSolucion.setTiempoSolucion(Integer.MAX_VALUE);
        mejorSolucion.setNombreAlgoritmo("Backtracking");

        // Se inicializa el hash solución y el de carga de procesadores
        HashMap<String, Integer> cargaProcesadores = new HashMap<>();
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
            cargaProcesadores.put(p, 0);
        }

        // Se convierte el HashMap en ArrayList
        List<Tarea> listaTareas = new ArrayList<>(tareas.values());
        listaTareas.sort((t1, t2) -> t1.getID().compareTo(t2.getID()));
        backtracking(0, listaTareas, solucionParcial, cargaProcesadores);
        return mejorSolucion;
    }

    /**
     * Metodo recursivo que ejecuta el algoritmo Backtracking para obtener la asignación de tareas tal que el tiempo de ejecución sea el mínimo.
     *
     * @param tareaIndex
     * @param listaTareas
     * @param solucionParcial
     * @param cargaProcesadores
     */
    //Recorriendo la lista de tareas se intenta asignar la primer tarea a cada procesador, creando una rama por cada procesador. Luego se asigna la segunda tarea a cada procesador, creando más ramas y así con las restantes para lograr las diferentes combinaciones. Se evalúa cada una y busca la mejor solución. Básicamente, va probando todas las opciones, retrocede si una no funciona bien, y sigue adelante hasta encontrar la distribución óptima.
    public void backtracking(int tareaIndex, List<Tarea> listaTareas, HashMap<String, ArrayList<Tarea>> solucionParcial, HashMap<String, Integer> cargaProcesadores) {
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

        for (String procesadorActual : procesadores.keySet()) {

            //CONDICIÓN 1: chequear si ya hay 2 criticas en la lista del procesador actual
            if (!tareaActual.isCritica() || procesadores.get(procesadorActual).getCantidadTareasCritias()<2) {
                //CONDICIÓN 2: si el procesador es no refrigerado, el tiempo de las tareas no debe superar el limite máximo ingresado
                if (procesadores.get(procesadorActual).isRefrigerado() || ((tareaActual.getTiempoDeEjecucion() + procesadores.get(procesadorActual).getTiempoEjecucion() <= LIMITE_PROCESADOR_NO_R))) {
                    // Agregar tarea actual al procesador actual
                    solucionParcial.get(procesadorActual).add(tareaActual);
                    procesadores.get(procesadorActual).setTiempoEjecucion(+ tareaActual.getTiempoDeEjecucion());
                    if(tareaActual.isCritica()) {
                        procesadores.get(procesadorActual).setCantidadTareasCritias(+1);
                    }
                    //PODA: si el tiempo maximo parcial supera al tiempo de la mejor solucion: se poda, ya que no tiene sentido recorrer el resto.
                    if (max(solucionParcial) < mejorSolucion.getTiempoSolucion()) {
                        backtracking(tareaIndex + 1, listaTareas, solucionParcial, cargaProcesadores);
                    }

                    // Quitar tarea actual del procesador actual
                    solucionParcial.get(procesadorActual).remove(solucionParcial.get(procesadorActual).size() - 1);
                    procesadores.get(procesadorActual).setTiempoEjecucion(- tareaActual.getTiempoDeEjecucion());

                    if(tareaActual.isCritica()) {
                        procesadores.get(procesadorActual).setCantidadTareasCritias(-1);
                    }

                }
            }

        }

    }

    /**
     * Obtiene el tiempo de la suma de tareas, del procesador con mayor carga.
     * COMPLEJIDAD: O(p*t)
     *
     * @param solucionParcial
     * @return
     */
    private Integer max(HashMap<String, ArrayList<Tarea>> solucionParcial) {
        Integer maxCarga = 0;
        for (String procesador : solucionParcial.keySet()) {
            Integer suma = 0;
            suma+=procesadores.get(procesador).getTiempoEjecucion();
            if (suma > maxCarga) {
                maxCarga = suma;
            }
        }
        return maxCarga;
    }


    /**
     * Guarda la solucion parcial pasado por parámetro en la mejor solución. Lo hace recorriendo el HashMap de solucion parciol, y las listas de tareas asignada a cada procesador
     * COMPLEJIDAD: O(p*t)
     *
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

    /**
     * (Resolución de etapa 2: Greedy). Asigna las tareas usando Greedy.
     * COMPLEJIDAD: O(t^2)
     * **Estrategia**: Primero, se inicializa un HashMap vacío solucionParcial para almacenar las asignaciones de tareas a cada
     * procesador y otro HashMap cargaProcesadores para almacenar la carga de trabajo de cada procesador,
     * aprovechando el acceso rápido de O(1) de estas estructuras.
     * Las tareas se ordenaron estrategicamente en orden descendente según su tiempo de ejecución para asignar primero las tareas
     * más largas al procesador con menor carga actual.
     * Siguiendo la estructura general de Greedy, se evalua la factibilidad determinando si es válido para nuestro
     * problema agregar el candidato seleccionado a la solución, segun las siguientes restricciones:
     * El límite de 2 tareas críticas por procesador y el tiempo máximo de ejecución permitido para procesadores no refrigerados.
     *
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    public Solucion asignarTareasGreedy(int tiempoLimitePNoRefrigerado) {
        Solucion solucion = new Solucion();
        solucion.setNombreAlgoritmo("Greedy");

        HashMap<String, Integer> cargaProcesadores = new HashMap<>();
        ArrayList tareasNoAsignadas = new ArrayList();
        int considerados = 0;

        //COMPLEJIDAD: O(P)
        //preparar HashMap solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
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
                // cehequea si es factible (si se cumplen las dos condiciones)
                if (esFactible(solucionParcial, tareaActual, procesadorMenorCarga, tiempoLimitePNoRefrigerado, cargaProcesadores)) {
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
            // si la tarea no fue asignada y no quedan procesadores por recorrer agrego la TareaActual a tareasNoAsignadas para mostrarla en la solucion
            if (!asignada && procesadoresRestantes.isEmpty()) {
                tareasNoAsignadas.add(tareaActual);
            }
        }

        solucion.setSolucion(solucionParcial);
        solucion.setMetrica(considerados);
        solucion.setTareasNoAsignadas(tareasNoAsignadas);
        return solucion;
    }

    /**
     * Analiza si es viable agregar la tarea actual en el procesador actual, basandose en las siguientes restricciones:
     * Ningún procesador podrá ejecutar más de 2 tareas crítica.
     * Los procesadores no refrigerados no podrán dedicar más de X tiempo de ejecución a
     * las tareas asignadas
     * COMPLEJIDAD: O(T)
     *
     * @param solucionParcial
     * @param tareaActual
     * @param procesador
     * @param tiempoLimitePNoRefrigerado
     * @return
     */
    private boolean esFactible(HashMap<String, ArrayList<Tarea>> solucionParcial, Tarea tareaActual, String procesador, int tiempoLimitePNoRefrigerado, HashMap<String, Integer> cargaProcesadores) {

        //CONDICIÓN 1: chequear si ya hay 2 criticas en la lista del procesador actual
        if (!tareaActual.isCritica() || !limiteCriticas(solucionParcial.get(procesador))) {

            //CONDICIÓN 2: si el procesador es no refrigerado, se chequea que al agregar la tarea actual no supere el limite de tiempo
            if (procesadores.get(procesador).isRefrigerado() || ((tareaActual.getTiempoDeEjecucion() + cargaProcesadores.get(procesador)) <= tiempoLimitePNoRefrigerado)) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Devuelve el ID del procesador con menor carga, de entre los considerados que contiene la lista pasada por parametro
     * COMPLEJIDAD: O(P)
     *
     * @param cargaProcesadores
     * @param procesadoresRestantes
     * @return String
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