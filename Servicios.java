package tpe;

import tpe.model.Procesador;
import tpe.model.Solucion;
import tpe.model.Tarea;
import tpe.model.Arbol;
import tpe.utils.CSVReaderCustom;

import java.util.*;

import static tpe.utils.TextColor.*;

/**
 * NO modificar la interfaz de esta clase ni sus métodos públicos.
 * Sólo se podrá adaptar el nombre de la clase "Tarea" según sus decisiones
 * de implementación.
 */
public class Servicios {
    private HashMap<String, Tarea> tareas = new HashMap<>();
    private HashMap<String, Procesador> procesadores = new HashMap<>();
    private Arbol arbolTareas = new Arbol();


    /* Complejidad temporal O(n):
        En el constructor el metodo readProcessors() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Procesadores.csv.
        El metodo siguiente readTasks() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Tareas.csv. Teniendo en cuenta
        que dentro del metodo se hace una insercion a un arbol que es O(h), suponiendo que es un arbol balanceado. Y tambien se hace una
         insercion a un HashMap que es O(1). Ya que se supone el peor caso, como conclusion la Complejidad temporal O(n). */
    public Servicios(String pathProcesadores, String pathTareas) {
        CSVReaderCustom reader = new CSVReaderCustom();
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

    public void imprimir() {
        arbolTareas.printPreOrder();
        for (String procesador : procesadores.keySet()) {
            System.out.println(procesador.toString());
        }
    }

    Integer LIMITE_PROCESADOR_NO_R;

    private Solucion mejorSolucion = new Solucion();

    //COMPLEJIDAD TEMPORAL:
    public Solucion asignarTareasBacktracking(int tiempoLimitePNoRefrigerado) {
        LIMITE_PROCESADOR_NO_R = tiempoLimitePNoRefrigerado;
        mejorSolucion.setTiempoSolucion(Integer.MAX_VALUE);

        //preparar hash solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
        }

        backtracking(tareas, solucionParcial);

        return mejorSolucion;
    }


    private void backtracking(HashMap<String, Tarea> tareasRestantes, HashMap<String, ArrayList<Tarea>> solucionParcial) {
        if (tareasRestantes.isEmpty()) {
            //si no quedan tareas por asignar, se llegó a una solución
            System.out.println("No hay tareas restantes");

            //obtenermos el tiempo de la solucion actual y lo comparamos con el mejor obtenido hasta el momento
            Integer tiempoSolucionActual = max(solucionParcial);
            System.out.println("solucionParcial: "+ solucionParcial);
            System.out.println("tiempoSolucionActual: "+ tiempoSolucionActual);
            System.out.println("tiempoMejorSolucion: "+ mejorSolucion.getTiempoSolucion());
            if (tiempoSolucionActual < mejorSolucion.getTiempoSolucion()) {
                System.out.println(YELLOW + "Solución actual es mejor" + RESET);
                System.out.println();
                mejorSolucion.setTiempoSolucion(tiempoSolucionActual);
                reemplazarMejorSolucion(solucionParcial);
            }
        } else {
            for (Map.Entry<String, Tarea> entry : tareasRestantes.entrySet()) {
                String tareaId = entry.getKey();
                Tarea tareaActual = entry.getValue();

                HashMap<String, Tarea> copiaTareasRestantes = new HashMap<>(tareasRestantes);
                copiaTareasRestantes.remove(tareaId);
                for (String procesador : procesadores.keySet()) {
                    int i = 0;

                    //CONDICIÓN 1: chequear si hay 2 criticas ya en la lista del procesador actual
                    if (!tareaActual.isCritica() || !limiteCriticas(solucionParcial.get(procesador))) {
                        //CONDICIÓN 2: si el procesador no es refrigerado, el tiempo de la tarea no debe superar el limite máximo ingresado
                        if (procesadores.get(procesador).isRefrigerado() || (tareaActual.getTiempoDeEjecucion() < LIMITE_PROCESADOR_NO_R)) {

                            //se contabiliza un estado nuevo al asignar la tarea al procesador
                            mejorSolucion.sumarMetrica();

                            solucionParcial.get(procesador).add(tareaActual);
                            System.out.println(tareaActual.toString() + " --> " + procesador);
                            System.out.println("solucionActual: "+ solucionParcial);

                            //poda: si el tiempo maximo parcial supera ya al mejor tiempo
                            if (max(solucionParcial) < mejorSolucion.getTiempoSolucion()) {
                                backtracking(copiaTareasRestantes, solucionParcial);
                            } else System.out.println(tareaActual + " se hizo PODA. TiempoActual: " + max(solucionParcial) + ", mejor solución: " + mejorSolucion.getTiempoSolucion());
                        } else System.out.println("salta por CONDICIÓN 2");
                    } else System.out.println("salta por CONDICIÓN 1");
                    System.out.println();
                    //quitar tarea actual del procesador actual
                    for (int j = 0; j < solucionParcial.get(procesador).size(); j++) {
                        if (solucionParcial.get(procesador).get(j).getID().equals(tareaActual.getID())) {
                            solucionParcial.get(procesador).remove(j);
                            break;
                        }
                    }
                    i++;
                }
            }
        }
    }

    /**
     * Devuelve el valor de tiempo del procesador que tenga mayor tiempo de ejecución.
     *
     * COMPLEJIDAD TEMPORAL: P x N (Proc)
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
     * @param tareasDelProcesador
     * @return
     */
    private boolean limiteCriticas(ArrayList<Tarea> tareasDelProcesador) {
        int contador = 0;
        for (Tarea t : tareasDelProcesador) {
            if (t.isCritica())
                contador++;
        }
        return contador >= 2;
    }

    private void reemplazarMejorSolucion(HashMap<String, ArrayList<Tarea>> solucionParcial){
        mejorSolucion.getHashSolucion().clear();

        // Copiar la solución parcial en la mejor solución
        for (Map.Entry<String, ArrayList<Tarea>> entry : solucionParcial.entrySet()) {
            String procesadorId = entry.getKey();
            ArrayList<Tarea> tareasProcesador = new ArrayList<>(entry.getValue()); // Copia profunda de las tareas

            mejorSolucion.getHashSolucion().put(procesadorId, tareasProcesador);
        }
    }

    public Solucion asignarTareasGreedy(int tiempoLimitePNoRefrigerado) {
        Solucion solucion = new Solucion();
        HashMap<String, Integer> cargaProcesadores = new HashMap<>();
        ArrayList tareasNoAsignadas = new ArrayList();
        int considerados = 0;

        //preparar hash solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
            cargaProcesadores.put(p,0);
        }

        //ordena lista de tareas
        List<Tarea> tareasOrdenadas= new ArrayList<>(tareas.values());
        tareasOrdenadas.sort((t1, t2) -> Integer.compare(t2.getTiempoDeEjecucion(), t1.getTiempoDeEjecucion()));

        Iterator<Tarea> itTareas = tareasOrdenadas.iterator();
        while (itTareas.hasNext()) {
            Tarea tareaActual = itTareas.next();
            considerados+=solucionParcial.size();

            //cargamos, en principio, todos los procesadores como posibles soluciones
            ArrayList<String> procesadoresRestantes = new ArrayList<>();
            for (String p : procesadores.keySet()) {
                procesadoresRestantes.add(p);
            }
            boolean asignada = false;
            while(!asignada && !procesadoresRestantes.isEmpty() ) {
                String procesadorMenorCarga = menorCarga(cargaProcesadores, procesadoresRestantes);
                if (esFactible(solucionParcial, tareaActual, procesadorMenorCarga, tiempoLimitePNoRefrigerado)) {
                    solucionParcial.get(procesadorMenorCarga).add(tareaActual);
                    asignada = true;
                }else {
                    //descartamos el procesador como solucion ya que no es factible, para la tarea actual
                    procesadoresRestantes.remove(procesadorMenorCarga);
                    considerados--;
                }
            }
            if(!asignada && procesadoresRestantes.isEmpty()){
                tareasNoAsignadas.add(tareaActual);
            }
        }

        solucion.setSolucion(solucionParcial);
        solucion.setMetrica(considerados);
        solucion.setTareasNoAsignadas(tareasNoAsignadas);
        return solucion;
    }

    private boolean esFactible(HashMap<String, ArrayList<Tarea>> solucionParcial, Tarea tareaActual, String procesador, int tiempoLimitePNoRefrigerado){

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
     * @param cargaProcesadores
     * @param procesadoresRestantes
     * @return
     */
    private String menorCarga(HashMap<String, Integer> cargaProcesadores, List<String> procesadoresRestantes){
        String idMenor = "";
        int menorCarga = Integer.MAX_VALUE;
        for (String procesador : cargaProcesadores.keySet()) {
            if (procesadoresRestantes.contains(procesador)){
                int cargaActual = cargaProcesadores.get(procesador);
                if (cargaActual < menorCarga){
                    menorCarga = cargaActual;
                    idMenor = procesador;
                }
            }
        }
        return idMenor;
    }











































    public static void asignarTareasGreedy(int[] tiemposTareas, int mProcesadores) {
        // Inicializamos las cargas de los procesadores en 0
        int[] cargaProcesadores = new int[mProcesadores];
        Arrays.fill(cargaProcesadores, 0);

        // Ordenamos las tareas en orden descendente (opcional pero recomendado)
        Arrays.sort(tiemposTareas);
        int n = tiemposTareas.length;
        for (int i = 0; i < n / 2; i++) {
            int temp = tiemposTareas[i];
            tiemposTareas[i] = tiemposTareas[n - i - 1];
            tiemposTareas[n - i - 1] = temp;
        }

        // Asignamos cada tarea al procesador con la menor carga actual
        for (int tarea : tiemposTareas) {
            // Encontramos el índice del procesador con la menor carga
            int indiceMenorCarga = 0;
            for (int i = 1; i < mProcesadores; i++) {
                if (cargaProcesadores[i] < cargaProcesadores[indiceMenorCarga]) {
                    indiceMenorCarga = i;
                }
            }
            // Asignamos la tarea a ese procesador
            cargaProcesadores[indiceMenorCarga] += tarea;
        }

        // Imprimimos la carga de cada procesador después de asignar todas las tareas
        for (int i = 0; i < mProcesadores; i++) {
            System.out.println("Carga del procesador " + (i + 1) + ": " + cargaProcesadores[i]);
        }
    }

    public static void main(String[] args) {
        int[] tiemposTareas = {2, 14, 4, 16, 6, 5};
        int mProcesadores = 3;

        asignarTareasGreedy(tiemposTareas, mProcesadores);
    }

}