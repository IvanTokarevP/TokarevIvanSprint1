-- Таблица пользователей
CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица домов 
CREATE TABLE home (
    home_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    user_id INTEGER NOT NULL REFERENCES "user"(user_id) ON DELETE CASCADE
);

-- Таблица комнат
CREATE TABLE room (
    room_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    home_id INTEGER NOT NULL REFERENCES home(home_id) ON DELETE CASCADE
);

-- Таблица светильников
CREATE TABLE light (
    light_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    brightness INTEGER CHECK (brightness BETWEEN 0 AND 100),
    color VARCHAR(50), 
    room_id INTEGER REFERENCES room(room_id) ON DELETE SET NULL
);

-- Таблица ворот
CREATE TABLE gate (
    gate_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    home_id INTEGER NOT NULL REFERENCES "home"(home_id) ON DELETE CASCADE
);

-- Таблица камер
CREATE TABLE camera (
    camera_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    url VARCHAR(255) NOT NULL,
    room_id INTEGER NOT NULL REFERENCES "room"(room_id) ON DELETE CASCADE
);

-- Таблица датчиков температуры
CREATE TABLE temperature_sensor (
    sensor_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255), 
    current_temp FLOAT,
    room_id INTEGER REFERENCES room(room_id) ON DELETE SET NULL
);

-- Таблица обогревателей 
CREATE TABLE heater (
    heater_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT FALSE,
    target_temp FLOAT,
    room_id INTEGER REFERENCES room(room_id) ON DELETE SET NULL
);

-- Таблица событий ет
CREATE TABLE event (
    event_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(user_id) ON DELETE CASCADE,
    device_type VARCHAR(50) NOT NULL, -- 'light', 'gate', 'camera', 'temperature_sensor', 'heater'
    device_id INTEGER NOT NULL,
    event_type VARCHAR(100) NOT NULL, -- например, 'turn_on', 'turn_off', 'temperature_reading'
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data JSONB -- 
);

