package tpe.model;

public class Procesador {

    private String ID;

    private String codigo;
    private boolean refrigerado;
    private int anoInicioFuncionamiento;
    private int cantidadTareasCritias;
    private int tiempoEjecucion;

    public Procesador(String ID, String codigo, boolean refrigerado, int anoInicioFuncionamiento) {
        this.ID = ID;
        this.codigo = codigo;
        this.refrigerado = refrigerado;
        this.anoInicioFuncionamiento = anoInicioFuncionamiento;
        this.cantidadTareasCritias = 0;
        this.tiempoEjecucion = 0;
    }

    public String getID() {
        return ID;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isRefrigerado() {
        return refrigerado;
    }

    public int getAnoInicioFuncionamiento() {
        return anoInicioFuncionamiento;
    }

    public int getTiempoEjecucion() {
        return tiempoEjecucion;
    }

    public void setTiempoEjecucion(int tiempoEjecucion) {
        this.tiempoEjecucion = this.tiempoEjecucion+tiempoEjecucion;
    }

    public int getCantidadTareasCritias() {
        return cantidadTareasCritias;
    }

    public void setCantidadTareasCritias(int cantidadTareasCritias) {
        this.cantidadTareasCritias = this.cantidadTareasCritias+cantidadTareasCritias;
    }
}
