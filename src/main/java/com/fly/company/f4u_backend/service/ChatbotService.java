package com.fly.company.f4u_backend.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fly.company.f4u_backend.model.Flight;
import com.fly.company.f4u_backend.model.Reservation;
import com.fly.company.f4u_backend.model.Seat;
import com.fly.company.f4u_backend.repository.FlightRepository;
import com.fly.company.f4u_backend.repository.ReservationRepository;
import com.fly.company.f4u_backend.repository.SeatRepository;

@Service
public class ChatbotService {

    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public ChatbotService(ReservationRepository reservationRepository, 
                         FlightRepository flightRepository,
                         SeatRepository seatRepository) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
        this.seatRepository = seatRepository;
        
        // Configurar RestTemplate con timeout de 30 segundos
        this.restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(30))
            .build();
        
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Procesa una pregunta del usuario sobre sus reservas
     */
    public String processQuestion(String userEmail, String question) {
        try {
            System.out.println("[Chatbot] Procesando pregunta para usuario: " + userEmail);
            System.out.println("[Chatbot] Pregunta: " + question);
            
            // 1. Obtener todas las reservas del usuario con información completa
            List<Reservation> reservations = reservationRepository.findByPasajeroEmail(userEmail);
            System.out.println("[Chatbot] Reservas encontradas: " + reservations.size());

            if (reservations.isEmpty()) {
                return "No tienes ninguna reserva registrada en el sistema.";
            }

            // 2. Construir el contexto con toda la información de las reservas
            System.out.println("[Chatbot] Construyendo contexto...");
            String context = buildReservationContext(reservations);
            System.out.println("[Chatbot] Contexto construido: " + context.length() + " caracteres");

            // 3. Crear el prompt para Gemini AI con capacidad de usar conocimiento general
            String systemPrompt = """
                Eres un asistente virtual inteligente de Fly For You (F4U), una aerolínea colombiana.
                Tu trabajo es ayudar a los usuarios con información sobre sus reservas de vuelos Y proporcionar
                información útil para viajeros usando tu amplio conocimiento sobre destinos, clima y cultura.
                
                CAPACIDADES:
                - Responder sobre las reservas del usuario con la información proporcionada
                - Proporcionar información general sobre destinos (clima típico, enchufes eléctricos, cultura, recomendaciones de vestimenta)
                - Dar consejos útiles para viajeros basados en el destino
                - Responder preguntas generales sobre viajes
                
                INSTRUCCIONES IMPORTANTES:
                1. Cuando te pregunten sobre las reservas del usuario, usa ÚNICAMENTE la información proporcionada en el contexto
                2. Cuando te pregunten sobre el destino (clima, enchufes, cultura, qué llevar, etc.), usa tu conocimiento general sobre ese lugar
                3. Para preguntas sobre clima y vestimenta, proporciona recomendaciones basadas en el clima típico del destino y la época del año
                4. Sé específico y útil - no digas "no tengo esa información" si es algo que conoces sobre el destino
                5. Sé amable, profesional y conversacional
                6. Incluye emojis relevantes (✈️, 📅, 💺, 🌡️, 🔌, 👕, etc.) para hacer la respuesta más amigable
                7. Responde siempre en español
                
                FORMATO DE RESPUESTA:
                - Responde SOLO en texto plano, sin usar asteriscos (*), guiones (-), ni formato markdown
                - Mantén tus respuestas breves: máximo UN párrafo de 3-4 oraciones
                - No uses listas con viñetas, escribe todo en formato de párrafo corrido
                - Sé conciso y directo
                
                CONTEXTO DE LAS RESERVAS DEL USUARIO:
                """ + context;

            // 4. Intentar llamar a la API de IA con fallback a respuesta simple
            System.out.println("[Chatbot] Llamando a la API de IA...");
            System.out.println("[Chatbot] API URL: " + geminiApiUrl);
            System.out.println("[Chatbot] API Key configurada: " + (geminiApiKey != null && !geminiApiKey.isEmpty()));
            
            try {
                String response = callGeminiAI(systemPrompt, question);
                System.out.println("[Chatbot] Respuesta recibida: " + response.substring(0, Math.min(100, response.length())) + "...");
                return response;
            } catch (Exception aiError) {
                System.err.println("[Chatbot] API de IA no disponible, usando respuesta simple: " + aiError.getMessage());
                // Fallback: generar respuesta simple basada en el contexto
                return generateSimpleResponse(reservations, question);
            }

        } catch (Exception e) {
            System.err.println("Error en ChatbotService: " + e.getMessage());
            e.printStackTrace();
            return "Lo siento, hubo un error al procesar tu pregunta. Por favor intenta de nuevo.";
        }
    }

    /**
     * Construye el contexto con toda la información de las reservas
     */
    private String buildReservationContext(List<Reservation> reservations) {
        StringBuilder context = new StringBuilder();
        
        context.append("TOTAL DE RESERVAS: ").append(reservations.size()).append("\n\n");

        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            
            context.append("=== RESERVA #").append(i + 1).append(" ===\n");
            context.append("Código de Reserva: ").append(r.getCodigoReservacion()).append("\n");
            context.append("Estado: ").append(r.getEstado()).append("\n");
            context.append("Estado de Pago: ").append(r.getEstadoPago()).append("\n");
            
            // Información del vuelo
            Flight flight = flightRepository.findById(r.getVueloId()).orElse(null);
            if (flight != null) {
                context.append("\nINFORMACIÓN DEL VUELO:\n");
                context.append("  - Número de Vuelo: ").append(flight.getNumeroVuelo()).append("\n");
                context.append("  - Fecha y Hora de Salida: ").append(flight.getFechaSalida()).append("\n");
                context.append("  - Fecha y Hora de Llegada: ").append(flight.getFechaLlegada()).append("\n");
                context.append("  - Duración: ").append(flight.getDuracionMinutos()).append(" minutos\n");
                context.append("  - Estado del Vuelo: ").append(flight.getEstado()).append("\n");
                
                if (flight.getPuertaEmbarque() != null) {
                    context.append("  - Puerta de Embarque: ").append(flight.getPuertaEmbarque()).append("\n");
                }
                
                if (flight.getTerminal() != null) {
                    context.append("  - Terminal: ").append(flight.getTerminal()).append("\n");
                }
                
                // Ciudad origen
                if (flight.getCiudadOrigen() != null) {
                    context.append("  - Origen: ").append(flight.getCiudadOrigen().getNombre())
                           .append(" (").append(flight.getCiudadOrigen().getCodigoIata()).append(")\n");
                    context.append("    País: ").append(flight.getCiudadOrigen().getPais()).append("\n");
                }
                
                // Ciudad destino
                if (flight.getCiudadDestino() != null) {
                    context.append("  - Destino: ").append(flight.getCiudadDestino().getNombre())
                           .append(" (").append(flight.getCiudadDestino().getCodigoIata()).append(")\n");
                    context.append("    País: ").append(flight.getCiudadDestino().getPais()).append("\n");
                }
            }
            
            // Información del asiento
            Seat seat = seatRepository.findById(r.getAsientoId()).orElse(null);
            if (seat != null) {
                context.append("\nINFORMACIÓN DEL ASIENTO:\n");
                context.append("  - Número de Asiento: ").append(seat.getNumeroAsiento()).append("\n");
                context.append("  - Clase: ").append(r.getClase()).append("\n");
            }
            
            // Información del pasajero
            context.append("\nINFORMACIÓN DEL PASAJERO:\n");
            context.append("  - Nombre: ").append(r.getPasajeroNombre()).append(" ").append(r.getPasajeroApellido()).append("\n");
            context.append("  - Email: ").append(r.getPasajeroEmail()).append("\n");
            context.append("  - Teléfono: ").append(r.getPasajeroTelefono()).append("\n");
            context.append("  - Documento: ").append(r.getPasajeroDocumentoTipo()).append(" ")
                   .append(r.getPasajeroDocumentoNumero()).append("\n");
            
            // Precios
            context.append("\nPRECIOS:\n");
            context.append("  - Precio del Asiento: $").append(r.getPrecioAsiento()).append("\n");
            context.append("  - Precio de Extras: $").append(r.getPrecioExtras()).append("\n");
            context.append("  - TOTAL: $").append(r.getPrecioTotal()).append("\n");
            
            // Extras incluidos
            context.append("\nEXTRAS INCLUIDOS:\n");
            context.append("  - Maleta de Cabina: ").append(r.getExtraMaletaCabina() ? "Sí" : "No").append("\n");
            context.append("  - Maleta en Bodega: ").append(r.getExtraMaletaBodega() ? "Sí" : "No").append("\n");
            context.append("  - Seguro 50%: ").append(r.getExtraSeguro50() ? "Sí" : "No").append("\n");
            context.append("  - Seguro 100%: ").append(r.getExtraSeguro100() ? "Sí" : "No").append("\n");
            context.append("  - Asistencia Especial: ").append(r.getExtraAsistenciaEspecial() ? "Sí" : "No").append("\n");
            
            // Método de pago
            context.append("\nMÉTODO DE PAGO: ").append(r.getMetodoPago()).append("\n");
            
            // Fechas de creación y modificación
            if (r.getFechaCreacion() != null) {
                context.append("Fecha de Creación: ").append(r.getFechaCreacion()).append("\n");
            }
            if (r.getFechaActualizacion() != null) {
                context.append("Última Modificación: ").append(r.getFechaActualizacion()).append("\n");
            }
            
            context.append("\n");
        }

        return context.toString();
    }

    /**
     * Llama a la API de Gemini AI
     */
    private String callGeminiAI(String systemPrompt, String userQuestion) {
        try {
            System.out.println("[Chatbot API] Iniciando llamada a la API de IA");
            
            // Validar configuración
            if (geminiApiKey == null || geminiApiKey.isEmpty()) {
                System.err.println("[Chatbot API] ERROR: API Key no configurada");
                return "Error de configuración: API Key no encontrada.";
            }
            if (geminiApiUrl == null || geminiApiUrl.isEmpty()) {
                System.err.println("[Chatbot API] ERROR: API URL no configurada");
                return "Error de configuración: API URL no encontrada.";
            }
            
            System.out.println("[Chatbot API] API Key presente: " + geminiApiKey.substring(0, 7) + "...");
            System.out.println("[Chatbot API] API URL: " + geminiApiUrl);
            System.out.println("[Chatbot API] API Key completa (para verificación): " + geminiApiKey);
            
            // Construir el payload para Gemini AI (compatible con formato OpenAI)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gemini-2.0-flash-exp");
            requestBody.put("max_tokens", 1500);
            requestBody.put("temperature", 0.7);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userQuestion));
            requestBody.put("messages", messages);
            
            System.out.println("[Chatbot API] Payload construido con " + messages.size() + " mensajes");
            System.out.println("[Chatbot API] Payload completo: " + new ObjectMapper().writeValueAsString(requestBody));

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + geminiApiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Gemini usa el endpoint /chat/completions igual que OpenAI
            String fullUrl = geminiApiUrl + "/chat/completions";
            System.out.println("[Chatbot API] Enviando petición a: " + fullUrl);

            // Hacer la llamada HTTP con manejo de excepciones
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
                );
            } catch (RestClientException e) {
                System.err.println("[Chatbot API] Error de conexión: " + e.getMessage());
                throw new RuntimeException("No se pudo conectar con la API de IA: " + e.getMessage(), e);
            }

            // Parsear la respuesta
            System.out.println("[Chatbot API] Status code: " + response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("[Chatbot API] Respuesta recibida, parseando JSON...");
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                System.out.println("[Chatbot API] JSON parseado exitosamente");
                JsonNode choices = jsonResponse.get("choices");
                
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null) {
                        String content = message.get("content").asText();
                        return content;
                    }
                }
            }

            return "Lo siento, no pude obtener una respuesta en este momento.";

        } catch (Exception e) {
            System.err.println("[Chatbot API] ERROR: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.err.println("[Chatbot API] Stack trace:");
            e.printStackTrace();
            throw new RuntimeException("Error al conectar con Gemini AI", e);
        }
    }

    /**
     * Genera una respuesta simple basada en las reservas (fallback cuando Gemini AI no está disponible)
     */
    private String generateSimpleResponse(List<Reservation> reservations, String question) {
        String questionLower = question.toLowerCase();
        
        // Respuesta para cantidad de reservas
        if (questionLower.contains("cuántas") || questionLower.contains("cuantas") || 
            questionLower.contains("número") || questionLower.contains("numero")) {
            return String.format("✈️ Tienes **%d %s** registrada%s en el sistema.\n\n" +
                "💡 Pregunta específica sobre alguna reserva para ver más detalles.",
                reservations.size(),
                reservations.size() == 1 ? "reserva" : "reservas",
                reservations.size() == 1 ? "" : "s");
        }
        
        // Respuesta para próximo vuelo
        if (questionLower.contains("próximo") || questionLower.contains("proximo") || 
            questionLower.contains("siguiente")) {
            if (reservations.isEmpty()) {
                return "No tienes reservas programadas.";
            }
            
            Reservation firstReservation = reservations.get(0);
            Flight flight = flightRepository.findById(firstReservation.getVueloId()).orElse(null);
            
            if (flight != null) {
                return String.format("🛫 Tu próximo vuelo:\n\n" +
                    "**Vuelo:** %s\n" +
                    "**Origen:** %s (%s)\n" +
                    "**Destino:** %s (%s)\n" +
                    "**Salida:** %s\n" +
                    "**Código de reserva:** %s\n" +
                    "**Estado:** %s",
                    flight.getNumeroVuelo(),
                    flight.getCiudadOrigen().getNombre(), flight.getCiudadOrigen().getCodigoIata(),
                    flight.getCiudadDestino().getNombre(), flight.getCiudadDestino().getCodigoIata(),
                    flight.getFechaSalida(),
                    firstReservation.getCodigoReservacion(),
                    firstReservation.getEstado());
            }
        }
        
        // Respuesta para detalles de reservas
        if (questionLower.contains("detalle") || questionLower.contains("información") || 
            questionLower.contains("informacion") || questionLower.contains("muéstrame") || 
            questionLower.contains("muestrame")) {
            
            StringBuilder response = new StringBuilder();
            response.append(String.format("📋 Tienes %d %s:\n\n", 
                reservations.size(), 
                reservations.size() == 1 ? "reserva" : "reservas"));
            
            for (int i = 0; i < Math.min(reservations.size(), 3); i++) {
                Reservation r = reservations.get(i);
                Flight flight = flightRepository.findById(r.getVueloId()).orElse(null);
                
                response.append(String.format("**Reserva #%d**\n", i + 1));
                response.append(String.format("- Código: %s\n", r.getCodigoReservacion()));
                response.append(String.format("- Estado: %s\n", r.getEstado()));
                response.append(String.format("- Total: $%s\n", r.getPrecioTotal()));
                
                if (flight != null) {
                    response.append(String.format("- Vuelo: %s → %s\n", 
                        flight.getCiudadOrigen().getNombre(),
                        flight.getCiudadDestino().getNombre()));
                    response.append(String.format("- Fecha: %s\n", flight.getFechaSalida()));
                }
                response.append("\n");
            }
            
            if (reservations.size() > 3) {
                response.append(String.format("... y %d reservas más.\n", reservations.size() - 3));
            }
            
            return response.toString();
        }
        
        // Respuesta para extras
        if (questionLower.contains("extra") || questionLower.contains("equipaje") || 
            questionLower.contains("maleta") || questionLower.contains("seguro")) {
            
            StringBuilder response = new StringBuilder();
            response.append("🎒 Extras en tus reservas:\n\n");
            
            for (int i = 0; i < reservations.size(); i++) {
                Reservation r = reservations.get(i);
                response.append(String.format("**Reserva %s:**\n", r.getCodigoReservacion()));
                
                List<String> extras = new ArrayList<>();
                if (r.getExtraMaletaCabina()) extras.add("Maleta de cabina");
                if (r.getExtraMaletaBodega()) extras.add("Maleta en bodega");
                if (r.getExtraSeguro50()) extras.add("Seguro 50%");
                if (r.getExtraSeguro100()) extras.add("Seguro 100%");
                if (r.getExtraAsistenciaEspecial()) extras.add("Asistencia especial");
                
                if (extras.isEmpty()) {
                    response.append("- Sin extras adicionales\n");
                } else {
                    response.append("- " + String.join(", ", extras) + "\n");
                }
                response.append("\n");
            }
            
            return response.toString();
        }
        
        // Respuesta genérica
        return String.format("💬 Hola! Tienes %d %s.\n\n" +
            "Puedo ayudarte con:\n" +
            "- Información sobre tus vuelos\n" +
            "- Detalles de tus reservas\n" +
            "- Extras incluidos\n" +
            "- Estados de pago\n\n" +
            "¿Qué te gustaría saber?",
            reservations.size(),
            reservations.size() == 1 ? "reserva" : "reservas");
    }
}
