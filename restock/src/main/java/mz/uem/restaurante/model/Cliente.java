package mz.uem.restaurante.model;

public class Cliente extends Usuario {
    private String telefone;

    public Cliente(int id, String nome, String username, String senha, String telefone) {
        super(id, nome, username, senha);
        this.telefone = telefone;
    }

    @Override
    public String getTipo() { return "Cliente"; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
