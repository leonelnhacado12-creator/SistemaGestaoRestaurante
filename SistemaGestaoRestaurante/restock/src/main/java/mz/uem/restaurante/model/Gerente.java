package mz.uem.restaurante.model;

public class Gerente extends Usuario {
    private String cargo;

    public Gerente(int id, String nome, String username, String senha, String cargo) {
        super(id, nome, username, senha);
        this.cargo = cargo;
    }

    @Override
    public String getTipo() { return "Gerente"; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}
