package mz.uem.restaurante.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Hash SHA-256 para não guardar senhas em texto simples na base de dados. */
public final class PasswordHasher {
    private PasswordHasher() { }

    public static String hash(String senha) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(senha.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                resultado.append(String.format("%02x", b));
            }
            return resultado.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 não está disponível na JVM.", ex);
        }
    }
}
