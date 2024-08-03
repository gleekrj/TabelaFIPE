package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.services.ConsumoApi;
import br.com.alura.TabelaFipe.services.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";


    public void exibeMenu(){

        var menu = """
                **** OPÇÕES ****
                Carros
                Motos
                Caminhões
                
                Digite umas das opções para consultar valores:
                """;

        System.out.println(menu);

        String opcao = leitura.nextLine();

        String endereco;
        if (opcao.toLowerCase().contains("carr"))
            endereco = URL_BASE + "carros/marcas";
        else if (opcao.toLowerCase().contains("mot"))
            endereco = URL_BASE + "motos/marcas";
        else if (opcao.toLowerCase().contains("camin"))
            endereco = URL_BASE + "caminhoes/marcas";
        else return;

        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(e-> System.out.println("Cód: " + e.codigo() + " - Marca: " + e.nome()));

        System.out.println("Digite o código da marca que deseja pesquisar:");

        var codigoMarca = leitura.nextLine();

        endereco += "/" + codigoMarca + "/modelos";

        json = consumo.obterDados(endereco);

        var modeloLista = conversor.obterDados(json, Modelos.class);
        System.out.println(modeloLista);

        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(m-> System.out.println("Cód: " + m.codigo() + " - Modelo: " + m.nome()));

        System.out.println("Digite parte do nome do veículo para pesquisa:");

        var nomeVeiculo = leitura.nextLine();

        modeloLista.modelos().stream()
                .filter(m-> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .forEach(m-> System.out.println("Cód: " + m.codigo()+ " - Descrição: " + m.nome()));

        System.out.println("Digite o código do modelo para consultar valores:");

        var codigoModelo = leitura.nextLine();

        endereco += "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i =0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            var veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        veiculos.forEach(System.out::println);
    }
}
