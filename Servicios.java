package tpe;

import tpe.model.Procesador;
import tpe.model.Tarea;
import tpe.model.Arbol;
import tpe.utils.CSVReaderCustom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

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

    public void asignacionTareas(int tiempoLimitePNoRefrigerado) {
        HashMap<String, ArrayList<Tarea>> solucion = new HashMap<>();
        //carga de procesadores en solucion
        for (String p : procesadores.keySet()) {
            solucion.put(p, new ArrayList<>());
        }
        HashMap<String, ArrayList<Tarea>> solucionFinal = new HashMap<>();
        Iterator<Tarea> itTareas = tareas.values().iterator();
        int[] mejorTiempo = new int[1];
        mejorTiempo[0] = Integer.MAX_VALUE;
        backtracking(itTareas, solucion, solucionFinal, tiempoLimitePNoRefrigerado, mejorTiempo);

        System.out.println("Backtracking");
        System.out.println("Solución obtenida: " + solucionFinal.toString());
            for (String s : solucionFinal.keySet()) {
                System.out.println(s.toString());
            }
        System.out.println("Tiempo de solución obtenida: " + mejorTiempo[0]);
        System.out.println("Métrica (cantidad de estados generados): " + cantidadDeEstados);
    }

    /*
Backtracking
Solución obtenida: cada procesador con las tareas asignadas.
Solución obtenida: tiempo máximo de ejecución.
Métrica para analizar el costo de la solución (cantidad de estados generados
    * */
    private void backtracking(Iterator<Tarea> itTareas, HashMap<String, ArrayList<Tarea>> solucionParcial, HashMap<String, ArrayList<Tarea>> solucionFinal, int tiempoLimitePNoRefrigerado, int[] mejorTiempo) {
        if (!itTareas.hasNext()) {
            Integer tiempoMaximo = max(solucionParcial);
            //compara el tiempo maximo parcial, con el tiempo de la mejor solución
            if (tiempoMaximo < mejorTiempo[0]) {
                mejorTiempo[0] = tiempoMaximo;
                System.out.println("solucionParcial: "+ solucionParcial);
                solucionFinal = solucionParcial;
                System.out.println("tiempoMaximo: "+ tiempoMaximo);
                System.out.println("mejorTiempo: "+ mejorTiempo[0]);
            }
        } else {
            Iterator<Tarea> copiaIt =itTareas;
            Tarea tareaActual = itTareas.next();
            for (String procesador : procesadores.keySet()) {
                int i = 0;

                //CONDICIÓN 1: chequear si hay 2 criticas ya en la lista del procesador actual
                if (!tareaActual.isCritica() || !limiteCriticas(solucionParcial.get(procesador))) {
                    //CONDICIÓN 2: si no es refrigerado, el tiempo de la tarea no debe superar el maximo ingresado(tiempo)
                    if (procesadores.get(procesador).isRefrigerado() || (tareaActual.getTiempoDeEjecucion() < tiempoLimitePNoRefrigerado)) {
                        solucionParcial.get(procesador).add(tareaActual);
                        //se contabiliza un estado nuevo al asignar la tarea al procesador
                        cantidadDeEstados++;
                        //poda: si el tiempo maximo parcial supera ya al mejor tiempo
                        if (max(solucionParcial) < mejorTiempo[0]) {
                            backtracking(copiaIt, solucionParcial, solucionFinal, tiempoLimitePNoRefrigerado, mejorTiempo);
                        }
                        //quitar tarea actual del procesador actual
                        for (int j = 0; j < solucionParcial.get(procesador).size(); j++) {
                            if (solucionParcial.get(procesador).get(j).getID().equals(tareaActual.getID())) {
                                solucionParcial.get(procesador).remove(j);
                                break;
                            }
                        }
                    }
                }
                i++;
            }
        }
    }

    /**
     * Devuelve el mayor tiempo de todos los procesadores
     *
     * @param solucion
     * @return
     */
    private Integer max(HashMap<String, ArrayList<Tarea>> solucion) {
        Integer maxCarga = 0;
        for (String procesador : solucion.keySet()) {
            Integer suma = 0;
            for (Tarea t : solucion.get(procesador)) {
                suma += t.getTiempoDeEjecucion();
            }
            if (suma > maxCarga) {
                maxCarga = suma;
            }
        }
        return maxCarga;
    }

    /**
     * Cheque en el lista de tareas ya asignadas al procesador pasada por parametro, si hay dos tareas crítica
     *
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