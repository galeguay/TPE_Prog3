package tpe;

import tpe.model.Solucion;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

	public static void main(String args[]) {
		Servicios servicios = new Servicios("datasets/Procesadores.csv", "datasets/TareasNuestro2.txt");
		Solucion solucion = servicios.asignarTareasGreedy(100);
		System.out.println(solucion.toString());
/*		// SERIVICIO 1
		System.out.println(servicios.servicio1("T3"));*/

/*		// SERIVICIO 2
		System.out.println("¿Desea ver las tareas criticas o no criticas? Escriba \"c\" para criticas y \"nc\"");
		boolean respuesta = inputServicio2();
		List<Tarea> salidaServicio2 = servicios.servicio2(respuesta);
		System.out.println(salidaServicio2);*/

		// SERIVICIO 3
/*		int priridadMenor = inputServicio3("Ingrese la prioridad menor del rango:");
		int priridadMayor = inputServicio3("Ingrese la prioridad mayor del rango:");
		List<Tarea> salidaServicio3 = servicios.servicio3(priridadMenor, priridadMayor);
		System.out.println(salidaServicio3);
*/


	}
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

	public static Integer inputServicio3(String mensaje) {
		System.out.println(mensaje);
		Integer valorIngresado = -1;
		BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
		try {
			valorIngresado = Integer.parseInt(entrada.readLine());
			if ((valorIngresado >= 1) && (valorIngresado <= 100)) {
				return valorIngresado;
			}else{
				return inputServicio3(mensaje);
			}
		}catch (Exception exc){
			System.out.println(exc);
		}
		return valorIngresado;
	}



}
