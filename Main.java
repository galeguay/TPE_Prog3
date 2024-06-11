package tpe;

import tpe.model.Solucion;
import tpe.model.Tarea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

	public static void main(String args[]) {
		//carga de tareas y procesadores
		Servicios servicios = new Servicios("datasets/Procesadores.csv", "datasets/Tareas.csv");

		// SERVICIO 1
		System.out.println("\nSERVICIO 1");
		System.out.println(servicios.servicio1("T3"));

		// SERVICIO 2
		System.out.println("\nSERVICIO 2\n");
		System.out.println("¿Desea ver las tareas criticas o no criticas? Escriba \"c\" para criticas y \"nc\"");
		boolean respuesta = inputServicio2();
		List<Tarea> salidaServicio2 = servicios.servicio2(respuesta);
		System.out.println(salidaServicio2);

		// SERVICIO 3
		System.out.println("\nSERVICIO 3\n");
		int priridadMenor = inputServicio3("Ingrese la prioridad MENOR del rango:", 1);
		int priridadMayor = inputServicio3("Ingrese la prioridad MAYOR del rango:", priridadMenor);
		List<Tarea> salidaServicio3 = servicios.servicio3(priridadMenor, priridadMayor);
		System.out.println(salidaServicio3.toString());

		//BACKTRACKING
		Solucion solucionBack = servicios.asignarTareasBacktracking(10);
		System.out.println(solucionBack.toString());

		//GREEDY
		Solucion solucionGreedy = servicios.asignarTareasGreedy(10);
		System.out.println(solucionGreedy.toString());

	}

	/**Se le solicita al usuario ingresar que tipo de tareas desea enlistar
	 * @return
	 */
	public static boolean inputServicio2(){
		String valor = "";
		boolean respuesta = true;
		BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
		try {
			valor = entrada.readLine();
			if(valor.equals("c")){
				respuesta = true;
			}else if(valor.equals("nc")) respuesta = false;
		}catch (Exception exc){
			System.out.println(exc);
		}
		return respuesta;
	}

	/**Se le solicita al usuario ingresar el rango de priridad deseado
	 *
	 * @param mensaje
	 * @return
	 */
	public static Integer inputServicio3(String mensaje, int minimo) {
		System.out.println(mensaje);
		Integer valorIngresado = -1;
		BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
		try {
			valorIngresado = Integer.parseInt(entrada.readLine());
			if ((valorIngresado >= minimo) && (valorIngresado <= 100)) {
				return valorIngresado;
			}else{
				return inputServicio3(mensaje, minimo);
			}
		}catch (Exception exc){
			System.out.println(exc);
		}
		return valorIngresado;
	}



}
