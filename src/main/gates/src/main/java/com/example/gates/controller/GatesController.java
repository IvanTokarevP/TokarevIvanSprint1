package com.example.gates.controller;

import com.example.gates.model.Gate;
import com.example.gates.model.GateInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/gates")
public class GatesController {

    // Списки для случайной генерации
    private static final List<String> GATE_NAMES = Arrays.asList("Северные", "Южные", "Западные", "Восточные", "Главные", "Запасные", "Автоматические", "Пешеходные");
    private static final List<String> LOCATIONS = Arrays.asList("Парковка", "Въезд", "Склад", "Цех", "Офис", "Территория А", "Территория Б");
    private static final List<String> STATUSES = Arrays.asList("open", "closed");

    private final Random random = ThreadLocalRandom.current();

    // Генерация случайного названия
    private String randomName() {
        return GATE_NAMES.get(random.nextInt(GATE_NAMES.size())) + " ворота " + random.nextInt(100);
    }

    // Генерация случайной локации
    private String randomLocation() {
        return LOCATIONS.get(random.nextInt(LOCATIONS.size()));
    }

    // Генерация случайного статуса
    private String randomStatus() {
        return STATUSES.get(random.nextInt(STATUSES.size()));
    }

    // Генерация случайных ворот по ID
    private Gate generateRandomGate(int id) {
        return new Gate(id, randomName(), randomLocation(), randomStatus());
    }

    // Генерация списка ворот случайного размера
    private List<Gate> generateRandomGates() {
        int count = 2 + random.nextInt(5); // от 2 до 6
        List<Gate> gates = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            gates.add(generateRandomGate(100 + i)); // ID начиная со 100
        }
        return gates;
    }

    // GET /gates
    @GetMapping
    public ResponseEntity<List<Gate>> getAllGates() {
        return ResponseEntity.ok(generateRandomGates());
    }

    // POST /gates
    @PostMapping
    public ResponseEntity<Gate> createGate(@RequestBody GateInput input) {
        // Простейшая валидация
        if (input.getName() == null || input.getName().trim().isEmpty() ||
            input.getLocation() == null || input.getLocation().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        int newId = 1000 + random.nextInt(9000); // случайный ID от 1000 до 9999
        String status = input.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "closed"; // статус по умолчанию
        }
        Gate gate = new Gate(newId, input.getName(), input.getLocation(), status);
        return ResponseEntity.status(HttpStatus.CREATED).body(gate);
    }

    // GET /gates/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Gate> getGateById(@PathVariable int id) {
        // Проверка допустимости ID (положительное число, не слишком большое)
        if (id <= 0 || id > 9999) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(generateRandomGate(id));
    }

    // PUT /gates/{id} (открыть/закрыть или полное обновление)
    @PutMapping("/{id}")
    public ResponseEntity<Gate> updateGate(@PathVariable int id, @RequestBody GateInput input) {
        if (id <= 0 || id > 9999) {
            return ResponseEntity.notFound().build();
        }
        // Валидация: если передан статус, используем его, иначе оставляем случайный
        String status = input.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = randomStatus();
        }
        // Если имя или локация не переданы, генерируем случайные
        String name = (input.getName() != null && !input.getName().trim().isEmpty()) ? input.getName() : randomName();
        String location = (input.getLocation() != null && !input.getLocation().trim().isEmpty()) ? input.getLocation() : randomLocation();

        Gate gate = new Gate(id, name, location, status);
        return ResponseEntity.ok(gate);
    }

    // DELETE /gates/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGate(@PathVariable int id) {
        if (id <= 0 || id > 9999) {
            return ResponseEntity.notFound().build();
        }
        // Всегда успешно
        return ResponseEntity.noContent().build();
    }
}