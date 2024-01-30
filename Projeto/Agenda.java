package Projeto;

import java.util.ArrayList;
import java.util.List;

public class Agenda {
    private List<Contato> contatos;
    
    public Agenda() {
        contatos = new ArrayList<Contato>();
    }

    public List<Contato> getContatos() {
        return contatos;
    }

    public void setContatos(List<Contato> contatos) {
        this.contatos = contatos;
    }

    public void adicionarContato(Contato contato) {
        contatos.add(contato);
    }

    public void removerContato(Long id) {
        contatos.removeIf(contato -> contato.getId().equals(id));
    }

    public Contato encontrarContatoPorId(long id) {
        for (Contato contato : contatos) {
            if (contato.getId() == id) {
                return contato;
            }
        }
        return null;
    }

    public void listarContatos() {
        if (contatos.isEmpty()) {
            System.out.println("Nenhum contato cadastrado.");
            return;
        }

        System.out.printf("%-5s | %-20s | %s\n", centerString(5, "Id"), centerString(25, "Nome"), centerString(15, "Telefones"));
        System.out.println("--------------------------------------------------------------");
        for (Contato contato : contatos) {
            List<Telefone> telefones = contato.getTelefones();
            String primeiroTelefone = telefones.isEmpty() ? "" : formatarTelefone(telefones.get(0));
            String nomeSobreNome = contato.getNome() + " " + contato.getSobreNome();

            System.out.printf("%-5d | %-25s | %s\n", contato.getId(), nomeSobreNome, primeiroTelefone);
            for (int i = 1; i < telefones.size(); i++) {
                System.out.printf("%-5s | %-25s | %s\n", "", "", formatarTelefone(telefones.get(i)));
            }

            System.out.println("--------------------------------------------------------------");
        }
    }

    public String formatarTelefone(Telefone telefone) {
        String numero = Long.toString(telefone.getNumero());
        String formatado = null;
        if (numero.length() == 9) {
            formatado = numero.substring(0, 5) + "-" + numero.substring(5);
        } else {
            formatado = " " + numero.substring(0, 4) + "-" + numero.substring(4);
        }
        return String.format("(%s) %s", telefone.getDdd(), formatado);
    }

    private String centerString(int width, String s) {
        return String.format("%-" + width + "s", String.format("%" + (s.length() + (width - s.length()) / 2) + "s", s));
    }
}