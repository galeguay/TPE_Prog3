package tpe.model;


import java.util.ArrayList;
import java.util.HashMap;

import static tpe.utils.TextColor.GREEN;
import static tpe.utils.TextColor.RED;
import static tpe.utils.TextColor.RESET;

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

    public void setTareaNoAsignada(Tarea tareaNoAsignada){
        if(!this.tareasNoAsignadas.contains(tareaNoAsignada))
            tareasNoAsignadas.add(tareaNoAsignada);
    }

    public String getNombreAlgoritmo() {
        return nombreAlgoritmo;
    }

    public void setNombreAlgoritmo(String nombreAlgoritmo) {
        this.nombreAlgoritmo = nombreAlgoritmo;
    }

    @Override
    public String toString() {
        String resp = "\n";

        if (!tareasNoAsignadas.isEmpty())
            resp += (RED + "NO ES SOLUCIÓN PARA TODAS LAS TAREAS (" + nombreAlgoritmo.toUpperCase() + ")");
        else
            resp += (GREEN + "SOLUCIÓN " + nombreAlgoritmo);

        resp += "\nSolución: " + enlistarTareas() +
                "\nMétrica: " + metrica +
                "\nTiempo de solución obtenida: " + tiempoSolucion;

        if (!tareasNoAsignadas.isEmpty())
            resp += "\nTareas no asignadas: " + enlistarTareasNoAsignadas() + "\n(Por exceder el máximo de tareas críticas. Ej: 3 tareas críticas para 1 procesador. Y/o por ejemplo, tareas que exceden el tiempo límite y no hay procesadores refrigerados)";
        return resp;
    }

    private String enlistarTareas() {
        String res = "";
        for (String p : solucion.keySet()) {
            ArrayList<Tarea> tareasActual = solucion.get(p);
            res += "\n  " + p + "[";
            for (int i = 0; i < tareasActual.size(); i++) {
                res += tareasActual.get(i).getNombre();
                if (i < tareasActual.size() - 1) {
                    res += ", ";
                }
            }
            res += "]";
        }
        return res;
    }

    private String enlistarTareasNoAsignadas() {
        String res = "";
        for (int i = 0; i < tareasNoAsignadas.size(); i++) {
            res += tareasNoAsignadas.get(i).getNombre();
            if (i < tareasNoAsignadas.size() - 1) {
                res += (", ");
            }
        }
        return res;
    }
}