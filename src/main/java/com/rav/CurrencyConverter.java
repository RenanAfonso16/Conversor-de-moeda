package com.rav;

import java.net.http.*;
import java.net.URI;
import java.io.IOException;
import java.util.*;
import com.google.gson.*;

public class CurrencyConverter {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/66e9fadcc4bd1cb2957c37fc/latest/USD";
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList("ARS", "BOB", "BRL", "CLP", "COP", "USD");

    private static Map<String, Double> rates = new HashMap<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Obtendo taxas de câmbio...");
        if (!fetchRates()) {
            System.out.println("Erro ao obter as taxas. Verifique sua conexão ou a chave da API.");
            return;
        }

        while (true) {
            System.out.println("\nConversor de Moedas");
            System.out.println("Moedas disponíveis: " + SUPPORTED_CURRENCIES);
            System.out.print("Digite a moeda de origem (ex: USD): ");
            String from = scanner.nextLine().toUpperCase();

            System.out.print("Digite a moeda de destino (ex: BRL): ");
            String to = scanner.nextLine().toUpperCase();

            System.out.print("Digite o valor: ");
            double amount = scanner.nextDouble();
            scanner.nextLine(); // consumir \n

            if (!SUPPORTED_CURRENCIES.contains(from) || !SUPPORTED_CURRENCIES.contains(to)) {
                System.out.println("Moeda não suportada. Tente novamente.");
                continue;
            }

            double result = convertCurrency(from, to, amount);
            System.out.printf("Resultado: %.2f %s = %.2f %s%n", amount, from, result, to);

            System.out.print("Deseja converter outra moeda? (s/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("s")) break;
        }

        scanner.close();
    }

    private static boolean fetchRates() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return false;
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject conversionRates = json.getAsJsonObject("conversion_rates");

            for (String code : SUPPORTED_CURRENCIES) {
                rates.put(code, conversionRates.get(code).getAsDouble());
            }

            return true;
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return false;
        }
    }

    private static double convertCurrency(String from, String to, double amount) {
        double rateFrom = rates.get(from);
        double rateTo = rates.get(to);
        return (amount / rateFrom) * rateTo;
    }
}
