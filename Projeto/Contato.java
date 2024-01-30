package Projeto;

import java.util.ArrayList;
import java.util.List;

public class Contato {
    private static Long ultimoId = 0L;
    private Long ultimoIdTelefone = 0L;
    private Long id;
    private String nome;
    private String sobreNome;
    private List<Telefone> telefones;

    public Contato() {
    }

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId() {
        this.id = ++ultimoId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUltimoIdTelefone() {
        return ++ultimoIdTelefone;
    }

    public void setUltimoIdTelefone(Long id) {
        this.ultimoIdTelefone = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobreNome() {
        return sobreNome;
    }

    public void setSobreNome(String sobreNome) {
        this.sobreNome = sobreNome;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(ArrayList<Telefone> telefones) {
        this.telefones = telefones;
    }

}
