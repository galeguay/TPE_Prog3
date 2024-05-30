package tpe.model;

public class Procesador {

    private String ID;

    private String codigo;
    private boolean refrigerado;
    private int anoInicioFuncionamiento;

    public Procesador(String ID, String codigo, boolean refrigerado, int anoInicioFuncionamiento) {
        this.ID = ID;
        this.codigo = codigo;
        this.refrigerado = refrigerado;
        this.anoInicioFuncionamiento = anoInicioFuncionamiento;
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

}
