package Projeto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static Agenda agenda = new Agenda();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("#######################################################");
        System.out.println("##### Santander Coders - 2023.2 - Java - Back End #####");
        System.out.println("#####   Projeto  Módulo 01 - Agenda de contatos   #####");
        System.out.println("#####            Lucas Cavalcanti Cruz            #####");
        System.out.println("#######################################################");
        

        System.out.print("Digite o seu nome para acessar sua agenda: ");
        String nomeUsuario = scanner.nextLine();
        String nomeArquivo = nomeUsuario + ".txt";
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo da agenda.");
                e.printStackTrace();
                return;
            }
        } else carregarContatos(nomeArquivo);
        System.out.println("Agenda de " + nomeUsuario + " carregada.");
        boolean sair = false;
        while (!sair) {
            exibirMenu();
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            switch (opcao) {
                case 1:
                    visualizarContatos();
                    break;
                case 2:
                    adicionarContato();
                    break;
                case 3:
                    removerContato();
                    break;
                case 4:
                    editarContato();
                    break;
                case 5:
                    salvarContatosEmArquivo(nomeArquivo);
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void exibirMenu() {
        System.out.println("##################");
        System.out.println("##### AGENDA #####");
        System.out.println("##################");
        System.out.println(">>>> Menu <<<<");
        System.out.println("1 - Visualizar Contatos");
        System.out.println("2 - Adicionar Contato");
        System.out.println("3 - Remover Contato");
        System.out.println("4 - Editar Contato");
        System.out.println("5 - Sair");
    }

    private static void visualizarContatos() {
        System.out.println(">>>> Contatos <<<<");
        agenda.listarContatos();
    }

    private static void adicionarContato() {
        String nome = null;
        String sobreNome = null;
        
        System.out.print("Digite o nome do contato: ");
        nome = scanner.nextLine();
        System.out.print("Digite o sobrenome do contato: ");
        sobreNome = scanner.nextLine();

        Contato novoContato = verificaContatoExistente(nome, sobreNome);

        if ( novoContato != null) {
            System.out.println("Contato Existente");
            System.out.println("Deseja editar o contato? (s/n)");
            if (scanner.nextLine().equalsIgnoreCase("s")) {
                editarContato(novoContato);
            }
        } else {            
            novoContato = new Contato(); 
            novoContato.setId();
            novoContato.setNome(nome);
            novoContato.setSobreNome(sobreNome);
            novoContato.setTelefones(new ArrayList<Telefone>());
            adicionarTelefone(novoContato);
            agenda.adicionarContato(novoContato);
            // Perguntar se deseja adicionar mais telefones
            System.out.print("Deseja adicionar outro número de telefone? (s/n): ");
            while (scanner.nextLine().equalsIgnoreCase("s")) {
                adicionarTelefone(novoContato);
                System.out.print("Deseja adicionar outro número de telefone? (s/n): ");
            }
        }
    }

    private static Contato verificaContatoExistente(String nome, String sobrenome) {
        for (Contato contato : agenda.getContatos()) {
            if (contato.getNome().equals(nome) && contato.getSobreNome().equals(sobrenome)) {
                return contato;
            }
        }
        return null;
    }

    private static void adicionarTelefone(Contato contato) {
        System.out.print("Digite o DDD: ");
        String ddd = scanner.nextLine();
        Long numeroTelefone = solicitarNumeroTelefone(ddd);
        Telefone telefone = new Telefone();
        telefone.setDdd(ddd);
        telefone.setNumero(numeroTelefone);
        telefone.setId(contato.getUltimoIdTelefone());

        contato.getTelefones().add(telefone);
    }

    private static Long solicitarNumeroTelefone(String ddd) {
        while (true) {
            try {
                System.out.print("Digite o número do telefone (formato: XXXXX-XXXX ou XXXX-XXXX): ");
                String numero = scanner.nextLine();

                if (validarNumeroTelefone(numero)) {
                    Long numeroValido = Long.parseLong(numero.replace("-", ""));
                    Contato contatoExistente = verificarTelefoneExistente(ddd, numeroValido);
                    if (contatoExistente != null) {
                        String nomeSobrenome = contatoExistente.getNome() + " " + contatoExistente.getSobreNome();
                        System.out.println("Esse número já está cadastrado. Pertence ao contato (ID " + contatoExistente.getId() + " - " + nomeSobrenome + ")");
                    } else {
                        return numeroValido;
                    }
                } else {
                    System.out.println("Formato inválido! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro: " + e.getMessage());
                System.out.println("Tente novamente.");
            }
        }
    }

    private static boolean validarNumeroTelefone(String numero) {
        Pattern pattern1 = Pattern.compile("\\d{5}-\\d{4}");
        Pattern pattern2 = Pattern.compile("\\d{4}-\\d{4}");
        Matcher matcher1 = pattern1.matcher(numero);
        Matcher matcher2 = pattern2.matcher(numero);

        return (matcher1.matches() || matcher2.matches());
    }

    public static Contato verificarTelefoneExistente(String ddd, Long numero) {
        for (Contato contato : agenda.getContatos()) {
            for (Telefone telefone : contato.getTelefones()) {
                if (telefone.getDdd().equals(ddd) && telefone.getNumero().equals(numero)) {
                    return contato;
                }
            }
        }
        return null;
    }

    public static void removerContato() {
        if (agenda.getContatos().isEmpty()) {
            System.out.println("Nenhum contato cadastrado.");
            return;
        }
    
        agenda.listarContatos();
    
        System.out.print("Digite o ID do contato a ser removido: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        
    
        Contato contatoARemover = null;
        for (Contato contato : agenda.getContatos()) {
            if (contato.getId().equals(id)) {
                contatoARemover = contato;
                break;
            }
        }
    
        if (contatoARemover != null) {
            String nomeSobrenome = contatoARemover.getNome() + " " + contatoARemover.getSobreNome();
            System.out.println("Tem certeza que deseja remover o contato " + contatoARemover.getId() + " - " + nomeSobrenome + "? (sim/não)");
            String confirmacao = scanner.nextLine();
            if (confirmacao.equalsIgnoreCase("sim")) {
                agenda.getContatos().remove(contatoARemover);
                System.out.println("Contato removido com sucesso.");
            } else {
                System.out.println("Remoção cancelada.");
            }
        } else {
            System.out.println("Contato não encontrado.");
        }
    }

    private static void editarContato(Contato contatoEditado) {
        System.out.println("Editar: 1 - Nome/Sobrenome");
        System.out.println("        2 - Telefones");
        int escolha = Integer.parseInt(scanner.nextLine());
        if (escolha == 1) {
            // Editar nome e sobrenome do contato
            System.out.print("Digite o novo nome: ");
            String novoNome = scanner.nextLine();
            contatoEditado.setNome(novoNome);
            System.out.print("Digite o novo sobrenome: ");
            String novoSobreNome = scanner.nextLine();
            contatoEditado.setSobreNome(novoSobreNome);
        } else if (escolha == 2) {
            System.out.println("1 - Editar número");
            System.out.println("2 - Adicionar número");
            System.out.println("3 - Excluir número");
            int editarListaTelefones = Integer.parseInt(scanner.nextLine());

            if (editarListaTelefones == 1) {
                editarTelefoneContato(contatoEditado);
            } else if (editarListaTelefones == 2) {
                adicionarTelefone(contatoEditado);
            } else if (editarListaTelefones == 3) {
                excluirTelefone(contatoEditado);
            }
        }
        System.out.println("Contato editado com sucesso.");
    }

    private static void editarContato() {
        if (agenda.getContatos().isEmpty()) {
            System.out.println("Nenhum contato cadastrado.");
            return;
        }
    
        agenda.listarContatos();
    
        System.out.print("Digite o ID do contato a ser editado: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Limpa o buffer do scanner

        Contato contatoEditado = agenda.encontrarContatoPorId(id);
    
        if (contatoEditado != null) {
            System.out.println("Tem certeza que deseja editar o contato " + contatoEditado.getId() + " - " + contatoEditado.getNome() + " " + contatoEditado.getSobreNome() + "? (sim/não)");
            String confirmacao = scanner.nextLine();
            if (confirmacao.equalsIgnoreCase("sim")) {
                System.out.println("Editar: 1 - Nome/Sobrenome");
                System.out.println("        2 - Telefones");
                int escolha = Integer.parseInt(scanner.nextLine());

                if (escolha == 1) {
                    // Editar nome e sobrenome do contato
                    System.out.print("Digite o novo nome: ");
                    String novoNome = scanner.nextLine();
                    contatoEditado.setNome(novoNome);
                    System.out.print("Digite o novo sobrenome: ");
                    String novoSobreNome = scanner.nextLine();
                    contatoEditado.setSobreNome(novoSobreNome);
                } else if (escolha == 2) {
                    System.out.println("1 - Editar número");
                    System.out.println("2 - Adicionar número");
                    System.out.println("3 - Excluir número");
                    int editarListaTelefones = Integer.parseInt(scanner.nextLine());
    
                    if (editarListaTelefones == 1) {
                        editarTelefoneContato(contatoEditado);
                    } else if (editarListaTelefones == 2) {
                        adicionarTelefone(contatoEditado);
                    } else if (editarListaTelefones == 3) {
                        excluirTelefone(contatoEditado);
                    }
                }
                System.out.println("Contato editado com sucesso.");
            } else {
                System.out.println("Edição cancelada.");
            }
        } else {
            System.out.println("Contato não encontrado.");
        }
        
    }

    private static void editarTelefoneContato(Contato contato) {
        // Listar telefones do contato
        System.out.println("Telefones:");
        List<Telefone> telefones = contato.getTelefones();
        for (Telefone telefone : telefones) {
            System.out.printf("ID: %d - %s\n", telefone.getId(), agenda.formatarTelefone(telefone));
        }

        // Pedir ao usuário para escolher um telefone pelo ID
        System.out.print("Digite o ID do telefone a ser editado: ");
        long idTelefone = Long.parseLong(scanner.nextLine());
        Telefone telefone = encontrarTelefonePorId(telefones, idTelefone);

        if (telefone == null) {
            System.out.println("Telefone não encontrado.");
            return;
        }
        // Solicitar novo número de telefone e atualizar
        System.out.print("Digite o DDD: ");
        String ddd = scanner.nextLine();
        Long numeroTelefone = solicitarNumeroTelefone(ddd);
        telefone.setDdd(ddd);
        telefone.setNumero(numeroTelefone);
    }

    private static Telefone encontrarTelefonePorId(List<Telefone> telefones, long id) {
        for (Telefone telefone : telefones) {
            if (telefone.getId() == id) {
                return telefone;
            }
        }
        return null;
    }

    private static void excluirTelefone(Contato contato) {

        // Listar telefones do contato
        System.out.println("Telefones:");
        List<Telefone> telefones = contato.getTelefones();
        for (Telefone telefone : telefones) {
            System.out.printf("ID: %d - %s\n", telefone.getId(), agenda.formatarTelefone(telefone));
        }

        // Pedir ao usuário para escolher um telefone pelo ID
        System.out.print("Digite o ID do telefone a ser excluído: ");
        long idTelefone = Long.parseLong(scanner.nextLine());
        Telefone telefone = encontrarTelefonePorId(telefones, idTelefone);

        if (telefone == null) {
            System.out.println("Telefone não encontrado.");
            return;
        }

        System.out.println("Tem certeza que deseja remover o telefone " + telefone.getId() + " - " + agenda.formatarTelefone(telefone) + "? (sim/não)");
        String confirmacao = scanner.nextLine();
        if (confirmacao.equalsIgnoreCase("sim")) {
            telefones.remove(telefone);
            System.out.println("Telefone removido com sucesso.");
        } else {
            System.out.println("Remoção cancelada.");
        }
    }

    private static String formatarContatoParaTexto(Contato contato) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID=").append(contato.getId()).append("|");
        sb.append("Nome=").append(contato.getNome()).append("|");
        sb.append("Sobrenome=").append(contato.getSobreNome()).append(" | ");
    
        List<Telefone> telefones = contato.getTelefones();
        for (int i = 0; i < telefones.size(); i++) {
            Telefone telefone = telefones.get(i);
            sb.append("IDTelefone=").append(telefone.getId()).append("|");
            sb.append("DDD=").append(telefone.getDdd()).append("|");
            sb.append("N=").append(telefone.getNumero());
            if (i < telefones.size() - 1) {
                sb.append(",");
            }
        }
    
        sb.append(" | ").append("ÚltimoID=").append(contato.getUltimoIdTelefone()-1);
        return sb.toString();
    }
    
    public static void salvarContatosEmArquivo(String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (Contato contato : agenda.getContatos()) {
                String contatoFormatado = formatarContatoParaTexto(contato);
                writer.write(contatoFormatado);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo.");
            e.printStackTrace();
        }
    }

    public static void carregarContatos(String nomeArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(" \\| ");
                String[] dadosContato = dados[0].split("\\|");
                String[] dadosTelefones = dados[1].split(",");
                Long ultimoId = Long.parseLong(dados[2].substring(9));
                Contato contato = new Contato();
                contato.setUltimoIdTelefone(ultimoId);
                Long idContato = null;
                String nome = "";
                String sobrenome = "";
                Long idTelefone = null;
                String ddd = "";
                Long numero = null;
                contato.setTelefones(new ArrayList<Telefone>());

                for (String parte : dadosContato) {
                    if (parte.startsWith("ID=")) {
                        idContato = Long.parseLong(parte.substring(3));
                        contato.setId(idContato);
                    } else if (parte.startsWith("Nome=")) {
                        nome = parte.substring(5);
                        contato.setNome(nome);
                    } else if (parte.startsWith("Sobrenome=")) {
                        sobrenome = parte.substring(10);
                        contato.setSobreNome(sobrenome);
                    }
                }
                
                for (String parteTelefones : dadosTelefones) {
                    Telefone telefone = new Telefone();
                    String[] parteTelefone = parteTelefones.split("\\|");
                    for (String dadosTelefone : parteTelefone) {
                        if (dadosTelefone.startsWith("IDTelefone=")) {
                            idTelefone = Long.parseLong(dadosTelefone.substring(11));
                            telefone.setId(idTelefone);
                        } else if (dadosTelefone.startsWith("DDD=")) {
                            ddd = dadosTelefone.substring(4);
                            telefone.setDdd(ddd);
                        } else if (dadosTelefone.startsWith("N=")) {
                            numero = Long.parseLong(dadosTelefone.substring(2));
                            telefone.setNumero(numero);
                        }                         
                    }
                    contato.getTelefones().add(telefone); 
                }

                agenda.adicionarContato(contato);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }   
}