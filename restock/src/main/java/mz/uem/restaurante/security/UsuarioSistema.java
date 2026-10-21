package mz.uem.restaurante.security;

/** Conta de acesso interna. A senha nunca é exposta, apenas o seu hash é persistido. */
public class UsuarioSistema {
    private int id;
    private String nome;
    private String username;
    private String senhaHash;
    private PerfilAcesso perfil;

    public UsuarioSistema(int id, String nome, String username, String senhaHash, PerfilAcesso perfil) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public PerfilAcesso getPerfil() { return perfil; }
    public void setPerfil(PerfilAcesso perfil) { this.perfil = perfil; }

    @Override
    public String toString() {
        return nome + " (" + perfil.getDescricao() + ")";
    }
}
