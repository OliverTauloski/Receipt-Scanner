import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReceiptScanner {
    public static void main(String[] args) {
        try {

            URL url = new URL("https://interview-task-api.mca.dev/qr-scanner-codes/alpha-qr-gFpwhsQ8fkY1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


            JSONParser parser = new JSONParser();
            JSONArray products = (JSONArray) parser.parse(response.toString());


            processReceiptDetails(products);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void processReceiptDetails(JSONArray products) {

        List<String> domesticProducts = new ArrayList<>();
        List<String> importedProducts = new ArrayList<>();
        Map<String, Double> productPrices = new HashMap<>();
        Map<String, String> productDescriptions = new HashMap<>();
        Map<String, String> productWeights = new HashMap<>();
        double totalDomesticCost = 0.0;
        double totalImportedCost = 0.0;


        for (Object productObj : products) {
            JSONObject product = (JSONObject) productObj;
            String name = (String) product.get("name");
            double price = (double) product.get("price");
            boolean isDomestic = (boolean) product.get("domestic");
            String description = (String) product.get("description");
            Object weightObj = product.get("weight");

            String weight;
            if (weightObj instanceof String) {
                weight = (String) weightObj;
            } else if (weightObj instanceof Long) {
                weight = Long.toString((Long) weightObj);
            } else {
                weight = "N/A";
            }

            if (isDomestic) {
                domesticProducts.add(name);
                totalDomesticCost += price;
            } else {
                importedProducts.add(name);
                totalImportedCost += price;
            }

            productPrices.put(name, price);
            productDescriptions.put(name, description);
            productWeights.put(name, weight);
        }

        Collections.sort(domesticProducts);
        Collections.sort(importedProducts);

        System.out.println("Domestic");
        for (String product : domesticProducts) {
            System.out.println(product);
            System.out.println("Price: $" + productPrices.get(product));
            System.out.println(productDescriptions.get(product));
            System.out.println("Weight: " + productWeights.get(product));
            System.out.println();
        }
        System.out.println("Domestic count: $" + totalDomesticCost);


        System.out.println("Imported");
        for (String product : importedProducts) {
            System.out.println(product);
            System.out.println("Price: $" + productPrices.get(product));
            System.out.println(productDescriptions.get(product));
            System.out.println("Weight: " + productWeights.get(product));
            System.out.println();
        }
        System.out.println("Imported count: $" + totalImportedCost);


        System.out.println("Domestic count: " + domesticProducts.size());
        System.out.println("Imported count: " + importedProducts.size());
    }

}
