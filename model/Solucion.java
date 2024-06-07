package tpe.model;

import java.util.ArrayList;
import java.util.HashMap;

import static tpe.utils.TextColor.GREEN;

public class Solucion {

    HashMap<String, ArrayList<Tarea>> solucion = new HashMap<>();
    int metrica;
    int tiempoSolucion;
    String nombreAlgoritmo = "";
    ArrayList<Tarea> tareasNoAsignadas = new ArrayList<>();

    public HashMap<String, ArrayList<Tarea>> getHashSolucion() {
        return solucion;
    }

    public void setSolucion(HashMap<String, ArrayList<Tarea>> solucion) {
        this.solucion = solucion;
        setTiempoSolucion(calcularTiempo());
    }

    public int getMetrica() {
        return metrica;
    }

    public void setMetrica(int metrica) {
        this.metrica = metrica;
    }

    public void sumarMetrica(){
        this.metrica++;
    }

    public int getTiempoSolucion() {
        return tiempoSolucion;
    }

    public void setTiempoSolucion(int tiempoSolucion) {
        this.tiempoSolucion = tiempoSolucion;
    }

    private Integer calcularTiempo() {
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

    public ArrayList<Tarea> getTareasNoAsignadas() {
        return tareasNoAsignadas;
    }

    public void setTareasNoAsignadas(ArrayList<Tarea> tareasNoAsignadas) {
        this.tareasNoAsignadas = tareasNoAsignadas;
    }

    @Override
    public String toString() {
        return GREEN + "SOLUCION "+ nombreAlgoritmo +
                "\nSolución: " + solucion +
                "\nMétrica=" + metrica +
                "\nTiempo de solución obtenida: " + tiempoSolucion +
                "\nTareas no asignadas : " + tareasNoAsignadas.toString() + "(Por máximo de tareas criticas. Ej 3 tareas criticas para 1 procesador. Tareas que exeden el timepo limite y no hay procesadores refrigerados)";
    }


}