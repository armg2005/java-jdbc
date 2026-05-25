package org.example;

import org.example.persistence.entity.Category;
import org.example.persistence.entity.QuizItem;
import org.example.service.QuizItemService;
import org.example.service.CategoryService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuizItemService quizItemService = new QuizItemService();
        CategoryService categoryService = new CategoryService(); // Instancia o service
        int opcao = 0;

        while (opcao != 8) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Inserir nova frase (item)");
            System.out.println("2) Inserir nova categoria");
            System.out.println("3) Listar categorias");
            System.out.println("4) Listar frases por categoria");
            System.out.println("5) Alterar frase (item)");
            System.out.println("6) Excluir frase (item)");
            System.out.println("7) Exportar para CSV");

            System.out.println("8) Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido.");
                continue;
            }

            switch (opcao) {
                case 1:
                    inserirNovaFrase(scanner, quizItemService);
                    break;
                case 2:
                    inserirCategoriaMenu(scanner, categoryService);
                    break;
                case 3: // <-- NOVO CASE
                    listarCategoriasMenu(categoryService);
                    break;
                case 4:
                    listarFrasesPorCategoriaMenu(scanner, quizItemService);
                    break;
                case 5:
                    alterarFraseMenu(scanner, quizItemService);
                    break;
                case 6:
                    excluirFraseMenu(scanner, quizItemService);
                    break;
                case 7:
                    exportarCSVMenu(quizItemService);
                    break;
                case 8:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
        scanner.close();
    }
    private static void inserirNovaFrase(Scanner scanner, QuizItemService service) {
        System.out.println("\n--- INSERIR NOVA FRASE ---");

        try {
            System.out.print("Digite a frase (sentence): ");
            String sentence = scanner.nextLine();

            System.out.print("Digite a resposta (0 - Falso, 1 - Verdadeiro): ");
            int answer = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite a dificuldade (0 - Fácil, 1 - Médio, 2 - Difícil): ");
            int level = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite o ID da Categoria: ");
            int categoryId = Integer.parseInt(scanner.nextLine());

            // Chama o service e imprime o retorno
            String resultado = service.cadastrarNovaFrase(sentence, answer, level, categoryId);
            System.out.println(resultado);


        } catch (NumberFormatException e) {
            System.out.println("Erro de entrada: Digite apenas números inteiros para 'answer', 'level' e 'categoria'.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void inserirCategoriaMenu(Scanner scanner, CategoryService service) {
        System.out.println("\n--- 2) INSERIR NOVA CATEGORIA ---");
        System.out.print("Digite o nome/descrição da nova categoria: ");
        String description = scanner.nextLine();

        // Chama o service e imprime o resultado
        String resultado = service.cadastrarNovaCategoria(description);
        System.out.println("\n" + resultado);
    }
    private static void listarCategoriasMenu(CategoryService service) {
        System.out.println("\n--- 3) LISTAR CATEGORIAS ---");

        List<Category> categorias = service.listarTodas();

        // Valida se a lista voltou vazia (nenhum registro no banco)
        if (categorias.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada no momento.");
        } else {
            // Exibição em formato de tabela simples
            System.out.printf("%-5s | %-40s\n", "ID", "DESCRIÇÃO");
            System.out.println("-------------------------------------------------");

            for (Category cat : categorias) {
                // %-5d = número inteiro alinhado à esquerda com 5 espaços
                // %-40s = string alinhada à esquerda com 40 espaços
                System.out.printf("%-5d | %-40s\n", cat.getId(), cat.getDescription());
            }
        }
    }
    private static void listarFrasesPorCategoriaMenu(Scanner scanner, QuizItemService service) {
        System.out.println("\n--- 4) LISTAR FRASES POR CATEGORIA ---");
        System.out.print("Digite o ID da Categoria (0 para listar TODAS): ");

        try {
            int categoryId = Integer.parseInt(scanner.nextLine());
            List<QuizItem> frases = service.listarPorCategoria(categoryId);

            if (frases.isEmpty()) {
                System.out.println("Nenhuma frase encontrada para esta categoria.");
            } else {
                System.out.printf("%-5s | %-15s | %-10s | %-10s | %-40s\n", "ID", "CATEGORIA", "RESPOSTA", "NÍVEL", "FRASE");
                System.out.println("-----------------------------------------------------------------------------------------");

                for (QuizItem item : frases) {
                    String resposta = item.getAnswer() == 1 ? "Verdadeiro" : "Falso";
                    String nivel = item.getLevel() == 0 ? "Fácil" : (item.getLevel() == 1 ? "Médio" : "Difícil");

                    System.out.printf("%-5d | %-15s | %-10s | %-10s | %-40s\n",
                            item.getId(),
                            item.getCategoryName(),
                            resposta,
                            nivel,
                            item.getSentence());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Erro: Digite um ID numérico válido.");
        } catch (IllegalArgumentException e) {
            // Captura o erro "Categoria não encontrada" lançado pelo Service [cite: 23]
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
    private static void alterarFraseMenu(Scanner scanner, QuizItemService service) {
        System.out.println("\n--- 5) ALTERAR FRASE ---");
        System.out.print("Digite o ID da frase que deseja alterar: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            // 1. Verifica se a frase existe ANTES de pedir os novos dados
            QuizItem fraseAtual = service.buscarPorId(id);

            if (fraseAtual == null) {
                System.out.println("Frase não encontrada."); // Conforme exigido na regra
                return; // Encerra o método e volta ao menu principal
            }

            // 2. Se existir, mostra a frase atual para contexto (opcional, mas boa prática)
            System.out.println("Frase atual: " + fraseAtual.getSentence());

            // 3. Solicita os novos valores
            System.out.print("Digite a nova frase (sentence): ");
            String sentence = scanner.nextLine();

            System.out.print("Digite a nova resposta (0 - Falso, 1 - Verdadeiro): ");
            int answer = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite o novo nível (0 - Fácil, 1 - Médio, 2 - Difícil): ");
            int level = Integer.parseInt(scanner.nextLine());

            // 4. Envia para o Service validar e atualizar
            String resultado = service.atualizarFrase(id, sentence, answer, level);
            System.out.println("\n" + resultado);

        } catch (NumberFormatException e) {
            System.out.println("Erro: Entrada inválida. Use apenas números inteiros para ID, resposta e nível.");
        }
    }
    private static void excluirFraseMenu(Scanner scanner, QuizItemService service) {
        System.out.println("\n--- 6) EXCLUIR FRASE ---");
        System.out.print("Digite o ID da frase que deseja excluir: ");

        try {
            int id = Integer.parseInt(scanner.nextLine());

            // 1. Verifica se a frase existe (reutilizando o buscarPorId da Etapa 5)
            org.example.persistence.entity.QuizItem fraseAtual = service.buscarPorId(id);

            // 2. Se não existir, exibe mensagem apropriada e cancela o fluxo
            if (fraseAtual == null) {
                System.out.println("Frase não encontrada."); // Conforme exigido
                return;
            }

            // 3. Mostra os dados para o usuário saber o que está apagando
            String resposta = fraseAtual.getAnswer() == 1 ? "Verdadeiro" : "Falso";
            System.out.println("\nDados da frase encontrada:");
            System.out.println("ID: " + fraseAtual.getId());
            System.out.println("Frase: " + fraseAtual.getSentence());
            System.out.println("Resposta: " + resposta);

            // 4. Solicita a confirmação antes de excluir
            System.out.print("\nATENÇÃO: Deseja realmente excluir esta frase? (S/N): ");
            String confirmacao = scanner.nextLine().trim().toUpperCase();

            // 5. Executa a deleção ou cancela a operação
            if (confirmacao.equals("S")) {
                String resultado = service.excluirFrase(id);
                System.out.println(resultado);
            } else {
                System.out.println("Operação de exclusão cancelada. A frase foi mantida.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Erro: Entrada inválida. Use apenas números inteiros para o ID.");
        }
    }
    private static void exportarCSVMenu(QuizItemService service) {
        System.out.println("\n--- 7) EXPORTAR PARA CSV ---");

        // Define o nome do arquivo na raiz do projeto
        String nomeArquivo = "quiz_export.csv";

        System.out.println("Iniciando exportação...");
        String resultado = service.exportarParaCSV(nomeArquivo);
        System.out.println(resultado);
    }
}