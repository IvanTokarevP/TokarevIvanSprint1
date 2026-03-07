package main

import (
	"context"
	"encoding/json"
	"log"
	"math/rand"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"strings"
	"syscall"
	"time"
)

// Camera представляет модель камеры
type Camera struct {
	ID       int    `json:"id"`
	Name     string `json:"name"`
	Location string `json:"location"`
	Status   string `json:"status"`
}

// CameraInput используется для создания новой камеры (тело POST-запроса)
type CameraInput struct {
	Name     string `json:"name"`
	Location string `json:"location"`
	Status   string `json:"status"`
}

// Глобальный генератор случайных чисел
var rng = rand.New(rand.NewSource(time.Now().UnixNano()))

// Возможные значения для случайной генерации
var (
	cameraNames = []string{"Вход", "Выход", "Парковка", "Склад", "Офис", "Коридор", "Лестница", "Лифт"}
	locations   = []string{"Главный вход", "Западное крыло", "Восточное крыло", "Северная сторона", "Южная сторона", "Подземный паркинг", "Второй этаж"}
	statuses    = []string{"active", "inactive", "maintenance"}
)

// generateRandomCamera создаёт камеру со случайными данными для заданного ID
func generateRandomCamera(id int) Camera {
	return Camera{
		ID:       id,
		Name:     randomChoice(cameraNames) + " " + strconv.Itoa(rng.Intn(100)),
		Location: randomChoice(locations),
		Status:   randomChoice(statuses),
	}
}

// generateRandomCameras создаёт случайное количество камер (от 2 до 6)
func generateRandomCameras() []Camera {
	count := 2 + rng.Intn(5) // 2..6
	cameras := make([]Camera, count)
	for i := 0; i < count; i++ {
		cameras[i] = generateRandomCamera(100 + i) // ID начиная со 100
	}
	return cameras
}

// randomChoice возвращает случайный элемент из среза строк
func randomChoice(slice []string) string {
	return slice[rng.Intn(len(slice))]
}

// Обработчики HTTP
type SurveillanceHandler struct{}

func (h *SurveillanceHandler) ListCameras(w http.ResponseWriter, r *http.Request) {
	cameras := generateRandomCameras()
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(cameras)
}

func (h *SurveillanceHandler) CreateCamera(w http.ResponseWriter, r *http.Request) {
	var input CameraInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}
	// Простейшая валидация
	if input.Name == "" || input.Location == "" {
		http.Error(w, "Name and location are required", http.StatusBadRequest)
		return
	}
	// Генерируем случайный ID (например, от 1000 до 9999)
	newID := 1000 + rng.Intn(9000)
	camera := Camera{
		ID:       newID,
		Name:     input.Name,
		Location: input.Location,
		Status:   input.Status,
	}
	// Если статус не указан, ставим "active" по умолчанию
	if camera.Status == "" {
		camera.Status = "active"
	}
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(camera)
}

func (h *SurveillanceHandler) GetCamera(w http.ResponseWriter, r *http.Request) {
	idStr := strings.TrimPrefix(r.URL.Path, "/surveillance/")
	id, err := strconv.Atoi(idStr)
	if err != nil || id <= 0 || id > 9999 { // ограничим допустимый диапазон ID
		http.Error(w, "Camera not found", http.StatusNotFound)
		return
	}
	// Генерируем случайную камеру с этим ID
	camera := generateRandomCamera(id)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(camera)
}

func (h *SurveillanceHandler) DeleteCamera(w http.ResponseWriter, r *http.Request) {
	idStr := strings.TrimPrefix(r.URL.Path, "/surveillance/")
	id, err := strconv.Atoi(idStr)
	if err != nil || id <= 0 {
		http.Error(w, "Invalid ID", http.StatusBadRequest)
		return
	}
	// Всегда возвращаем 204 (успешное удаление)
	w.WriteHeader(http.StatusNoContent)
}

func main() {
	handler := &SurveillanceHandler{}

	// Маршрутизация
	http.HandleFunc("/surveillance", func(w http.ResponseWriter, r *http.Request) {
		switch r.Method {
		case http.MethodGet:
			handler.ListCameras(w, r)
		case http.MethodPost:
			handler.CreateCamera(w, r)
		default:
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		}
	})

	http.HandleFunc("/surveillance/", func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/surveillance/" {
			http.NotFound(w, r)
			return
		}
		switch r.Method {
		case http.MethodGet:
			handler.GetCamera(w, r)
		case http.MethodDelete:
			handler.DeleteCamera(w, r)
		default:
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		}
	})

	// Настройка HTTP-сервера
	server := &http.Server{
		Addr:         ":8080",
		ReadTimeout:  5 * time.Second,
		WriteTimeout: 10 * time.Second,
		IdleTimeout:  120 * time.Second,
	}

	// Graceful shutdown
	stop := make(chan os.Signal, 1)
	signal.Notify(stop, os.Interrupt, syscall.SIGTERM)

	go func() {
		log.Println("Сервер запущен на порту 8080 (режим случайной генерации данных)")
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Ошибка запуска сервера: %v", err)
		}
	}()

	<-stop
	log.Println("Завершение работы сервера...")
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := server.Shutdown(ctx); err != nil {
		log.Fatalf("Ошибка при остановке сервера: %v", err)
	}
	log.Println("Сервер остановлен")
}
