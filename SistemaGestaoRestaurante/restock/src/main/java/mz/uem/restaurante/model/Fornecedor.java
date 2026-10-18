package mz.uem.restaurante.model;

public class Fornecedor {
 private int id; private String nome; private String telefone; private String email; private boolean activo;
 public Fornecedor(int id,String nome,String telefone,String email,boolean activo){this.id=id;this.nome=nome;this.telefone=telefone;this.email=email;this.activo=activo;}
 public int getId(){return id;} public String getNome(){return nome;} public String getTelefone(){return telefone;} public String getEmail(){return email;} public boolean isActivo(){return activo;}
 public void setNome(String v){nome=v;} public void setTelefone(String v){telefone=v;} public void setEmail(String v){email=v;} public void setActivo(boolean v){activo=v;}
 @Override public String toString(){return nome;}
}
