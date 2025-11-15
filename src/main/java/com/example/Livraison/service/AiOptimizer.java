package com.example.Livraison.service;

import com.example.Livraison.model.Delivery;
import com.example.Livraison.model.Vehicule;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("aiOptimizer")  // تأكد أن الاسم مطابق للـ @Qualifier
@ConditionalOnProperty(prefix = "ai.ollama", name = "base-url", matchIfMissing = true)
public class AiOptimizer implements TourOptimizer {

    private static final Logger LOG = LoggerFactory.getLogger(AiOptimizer.class);

    private final OllamaChatModel chatModel;

    public AiOptimizer(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public List<Delivery> calculateOptimalTour(List<Delivery> deliveries, Vehicule vehicule) {
        if (deliveries == null || deliveries.isEmpty()) {
            return Collections.emptyList();
        }

        String prompt = buildPrompt(deliveries, vehicule);

        String response;
        try {
            response = this.chatModel.call(prompt);
            LOG.info("AI response: {}", response);

        } catch (Exception e) {
            LOG.error("AI optimizer call failed, falling back to input order", e);
            return deliveries;
        }
        return parseResponse(response, deliveries);
    }

    @Override
    public double getTotalDistance(List<Delivery> deliveries) {
        if (deliveries == null || deliveries.size() < 2)
            return 0.0;
        double total = 0.0;
        for (int i = 0; i < deliveries.size() - 1; i++) {
            double dx = deliveries.get(i).getGpsLat() - deliveries.get(i + 1).getGpsLat();
            double dy = deliveries.get(i).getGpsLon() - deliveries.get(i + 1).getGpsLon();
            total += Math.sqrt(dx * dx + dy * dy);
        }
        return total;
    }

    private String buildPrompt(List<Delivery> deliveries, Vehicule vehicule) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                You are an AI logistics optimizer.
                Given the following deliveries with GPS coordinates and basic attributes, suggest the best delivery order
                to minimize total distance and expected delays. Return ONLY a JSON array of delivery IDs in the optimal order.
                Example valid response: [101, 103, 99]
                Do not include any extra text.
                """);

        sb.append("\nDeliveries:\n");
        for (Delivery d : deliveries) {
            Long id = d.getId();
            double lat = d.getGpsLat();
            double lon = d.getGpsLon();
            sb.append(String.format("{\"id\": %s, \"lat\": %.6f, \"lon\": %.6f},\n",
                    id == null ? "null" : id.toString(), lat, lon));
        }

        sb.append(String.format("Vehicle: {\"id\": %d, \"capaciteMaxKg\": %.3f, \"capaciteMaxM3\": %.3f}\n",
                vehicule != null ? vehicule.getId() : -1,
                vehicule != null ? vehicule.getCapaciteMaxKg() : 0.0,
                vehicule != null ? vehicule.getCapaciteMaxM3() : 0.0));

        return sb.toString();
    }

    private List<Delivery> parseResponse(String response, List<Delivery> deliveries) {
        if (response == null || response.isBlank())
            return deliveries;
        List<Delivery> ordered = new ArrayList<>();
        try {
            String cleaned = response.trim();
            int start = cleaned.indexOf('[');
            int end = cleaned.lastIndexOf(']');
            if (start >= 0 && end > start) {
                cleaned = cleaned.substring(start + 1, end);
            }
            String[] parts = cleaned.split(",");
            Map<Long, Delivery> byId = new HashMap<>();
            for (Delivery d : deliveries) {
                if (d.getId() != null)
                    byId.put(d.getId(), d);
            }
            Set<Long> seen = new HashSet<>();
            for (String p : parts) {
                String s = p.replaceAll("[^0-9]", "");
                if (s.isEmpty())
                    continue;
                long id = Long.parseLong(s);
                if (byId.containsKey(id) && !seen.contains(id)) {
                    ordered.add(byId.get(id));
                    seen.add(id);
                }
            }
            for (Delivery d : deliveries) {
                if (d.getId() == null || !seen.contains(d.getId())) {
                    ordered.add(d);
                }
            }
            return ordered.isEmpty() ? deliveries : ordered;
        } catch (Exception e) {
            e.printStackTrace();
            return deliveries;
        }
    }
}
