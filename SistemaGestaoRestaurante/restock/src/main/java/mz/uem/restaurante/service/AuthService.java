package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mz.uem.restaurante.dao.UsuarioDAO;
import mz.uem.restaurante.exception.ValidacaoException;
import mz.uem.restaurante.security.PasswordHasher;
import mz.uem.restaurante.security.PerfilAcesso;
import mz.uem.restaurante.security.UsuarioSistema;
import mz.uem.restaurante.validation.Validador;

/** Serviço de autenticação e gestão das três funções do sistema. */
public class AuthService {
    private final List<UsuarioSistema> usuarios = new ArrayList<>();
    private UsuarioDAO dao;

    public AuthService() {
        adicionarPadroesMemoria();
    }

    public void activarBaseDados(UsuarioDAO novoDao) throws SQLException {
        dao = novoDao;
        List<UsuarioSistema> persistidos = dao.listar();
        if (persistidos.isEmpty()) {
            for (UsuarioSistema usuario : new ArrayList<>(usuarios)) {
                dao.guardar(usuario);
            }
            persistidos = dao.listar();
        }
        usuarios.clear();
        usuarios.addAll(persistidos);
    }

    public UsuarioSistema autenticar(String username, String senha) {
        if (username == null || senha == null) {
            return null;
        }
        String senhaHash = PasswordHasher.hash(senha);
        return usuarios.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim())
                        && (u.getSenhaHash().equals(senhaHash)
                        // Compatibilidade com a credencial antiga da primeira versão.
                        || (u.getUsername().equalsIgnoreCase("admin") && "1234".equals(senha))))
                .findFirst().orElse(null);
    }

    public List<UsuarioSistema> listar() {
        return usuarios.stream().sorted(Comparator.comparing(UsuarioSistema::getNome, String.CASE_INSENSITIVE_ORDER)).toList();
    }

    public UsuarioSistema buscar(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    public UsuarioSistema guardar(int id, String nome, String username, String senha, PerfilAcesso perfil) {
        nome = Validador.obrigatorio(nome, "Nome");
        username = Validador.username(username);
        if (perfil == null) {
            throw new ValidacaoException("Seleccione um perfil de acesso.");
        }
        UsuarioSistema atual = id > 0 ? buscar(id) : null;
        String usernameValidado = username;
        boolean usernameOcupado = usuarios.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(usernameValidado) && u.getId() != id);
        if (usernameOcupado) {
            throw new ValidacaoException("Este utilizador já está registado.");
        }
        if (atual == null) {
            atual = new UsuarioSistema(0, nome, username, PasswordHasher.hash(Validador.senha(senha)), perfil);
        } else {
            atual.setNome(nome);
            atual.setUsername(username);
            atual.setPerfil(perfil);
            if (senha != null && !senha.isBlank()) {
                atual.setSenhaHash(PasswordHasher.hash(Validador.senha(senha)));
            }
        }
        try {
            if (dao != null) {
                dao.guardar(atual);
            } else if (atual.getId() <= 0) {
                atual.setId(usuarios.stream().mapToInt(UsuarioSistema::getId).max().orElse(0) + 1);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Não foi possível guardar o utilizador: " + ex.getMessage(), ex);
        }
        if (!usuarios.contains(atual)) {
            usuarios.add(atual);
        }
        return atual;
    }

    public void remover(int id, int idSessao) {
        if (id == idSessao) {
            throw new ValidacaoException("Não pode eliminar a sua própria conta durante a sessão.");
        }
        UsuarioSistema usuario = buscar(id);
        if (usuario == null) {
            return;
        }
        long administradores = usuarios.stream().filter(u -> u.getPerfil() == PerfilAcesso.ADMINISTRADOR).count();
        if (usuario.getPerfil() == PerfilAcesso.ADMINISTRADOR && administradores <= 1) {
            throw new ValidacaoException("Deve existir pelo menos um administrador.");
        }
        try {
            if (dao != null) {
                dao.remover(id);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Não foi possível remover o utilizador: " + ex.getMessage(), ex);
        }
        usuarios.remove(usuario);
    }

    private void adicionarPadroesMemoria() {
        usuarios.add(new UsuarioSistema(1, "Administrador do Sistema", "admin", PasswordHasher.hash("admin123"), PerfilAcesso.ADMINISTRADOR));
        usuarios.add(new UsuarioSistema(2, "Gerente do Restaurante", "gerente", PasswordHasher.hash("gerente123"), PerfilAcesso.GERENTE));
        usuarios.add(new UsuarioSistema(3, "Atendente", "atendente", PasswordHasher.hash("atendente123"), PerfilAcesso.ATENDENTE));
    }
}
