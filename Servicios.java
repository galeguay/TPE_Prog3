package tpe;

import tpe.model.Procesador;
import tpe.model.Tarea;
import tpe.model.Arbol;
import tpe.utils.CSVReaderCustom;
import java.util.ArrayList;
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
	private Arbol arbolTareas=new Arbol();


	/* Complejidad temporal O(n):
		En el constructor el metodo readProcessors() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Procesadores.csv.
		El metodo siguiente readTasks() es O(n) ya que depende de la cantidad de lineas que tiene el archivo Tareas.csv. Teniendo en cuenta
		que dentro del metodo se hace una insercion a un arbol que es O(h), suponiendo que es un arbol balanceado. Y tambien se hace una
		 insercion a un HashMap que es O(1). Ya que se supone el peor caso, como conclusion la Complejidad temporal O(n). */
	public Servicios(String pathProcesadores, String pathTareas) {
		CSVReaderCustom reader = new CSVReaderCustom();
		reader.readProcessors(pathProcesadores, procesadores);
		reader.readTasks(pathTareas,arbolTareas,tareas);
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
		for(String IDtarea : tareas.keySet()){
			if(esCritica){
				if(tareas.get(IDtarea).isCritica())
					lista.add(tareas.get(IDtarea));
			}else {
				if(!tareas.get(IDtarea).isCritica())
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

	public void imprimir(){
		arbolTareas.printPreOrder();
		for(String procesador: procesadores.keySet()){
			System.out.println(procesador.toString());
		}
	}
}
