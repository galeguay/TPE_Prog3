package tpe;

import tpe.model.Procesador;
import tpe.model.Tarea;
import tpe.model.Arbol;
import tpe.utils.CSVReaderCustom;

import java.util.*;
import java.util.stream.Collectors;

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

    int cantidadDeEstados = 0;
    //este contendra la soolucion final, no es necesario inicializarlo ya que sera una copia de la mejor solucion parcial
    HashMap<String, ArrayList<Tarea>> mejorSolucion;
    int tiempoMejorSolucion;
    Integer LIMITE_PROCESADOR_NO_R;


    public void asignacionTareas(int tiempoLimitePNoRefrigerado) {
        LIMITE_PROCESADOR_NO_R = tiempoLimitePNoRefrigerado;
        tiempoMejorSolucion = Integer.MAX_VALUE;

        //preparar hash solucion vacio que contendra el id de los procesadores con la lista de tareas a ejecutar
        HashMap<String, ArrayList<Tarea>> solucionParcial = new HashMap<>();
        for (String p : procesadores.keySet()) {
            solucionParcial.put(p, new ArrayList<>());
        }

/*        Set<String> keys = tareas.keySet();
        List<String> tareaList = new ArrayList<>(keys);
        for(String key : keys){
            tareaList.add();
        }*/

        backtracking(tareas, solucionParcial);

        System.out.println("FINAL DE Backtracking");
        System.out.println("Solución obtenida: " + mejorSolucion.toString());
        System.out.println("Tiempo de solución obtenida: " + tiempoMejorSolucion);
        System.out.println("Métrica (cantidad de estados generados): " + cantidadDeEstados);
    }

    private void backtracking(HashMap<String, Tarea> tareasRestantes, HashMap<String, ArrayList<Tarea>> solucionParcial) {
        if (tareasRestantes.isEmpty()) {
            //si no quedan tareas por asignar, se llegó a una solución
            System.out.println("No hay tareas restantes");

            //obtenermos el tiempo de la solucion actual y lo comparamos con el mejor obtenido hasta el momento
            Integer tiempoSolucionActual = max(solucionParcial);
            System.out.println("solucionParcial: "+ solucionParcial);
            System.out.println("tiempoSolucionActual: "+ tiempoSolucionActual);
            System.out.println("tiempoMejorSolucion: "+ tiempoMejorSolucion);
            if (tiempoSolucionActual < tiempoMejorSolucion) {
                tiempoMejorSolucion = tiempoSolucionActual;
                mejorSolucion = new HashMap<>(solucionParcial); // Copia profunda
                System.out.println("Solución actual es mejor");
                System.out.println();
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
                            cantidadDeEstados++;

                            solucionParcial.get(procesador).add(tareaActual);
                            System.out.println(tareaActual.toString() + " --> " + procesador);

                            //poda: si el tiempo maximo parcial supera ya al mejor tiempo
                            if (max(solucionParcial) < tiempoMejorSolucion) {
                                backtracking(copiaTareasRestantes, solucionParcial);
                            } else System.out.println(tareaActual.toString() + " salta por PODA. TiempoActual:" + max(solucionParcial) + ", mejor solución: " + tiempoMejorSolucion);
                        } else System.out.println("salta por CONDICIÓN 2");
                    } else System.out.println("salta por CONDICIÓN 1");

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
}