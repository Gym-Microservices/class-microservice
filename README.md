# 🏋️ Class Microservice

## 📋 Descripción

Microservicio para la gestión de clases del gimnasio (Puerto 8084). Permite programar clases, gestionar inscripciones de miembros y reservar equipamiento necesario para las clases.

## 🔗 Endpoints

### 📅 Gestión de Clases

- `POST /api/classes` - Programar una nueva clase
- `GET /api/classes` - Obtener todas las clases
- `GET /api/classes/{id}` - Obtener clase por ID
- `PUT /api/classes/{id}` - Actualizar información de una clase
- `DELETE /api/classes/{id}` - Eliminar una clase

### 🔍 Consultas Específicas

- `GET /api/classes/coach/{coachId}` - Obtener clases por entrenador
- `GET /api/classes/schedule?start={fecha}&end={fecha}` - Obtener clases por rango de fechas

### 👥 Gestión de Inscripciones

- `POST /api/classes/{id}/enroll/{memberId}` - Inscribir miembro a una clase
- `POST /api/classes/{id}/unenroll/{memberId}` - Desinscribir miembro de una clase

### 🏃‍♂️ Reserva de Equipamiento

- `POST /api/classes/{id}/equipment/{equipmentId}/reserve?quantity={cantidad}` - Reservar equipamiento para una clase

## 🛠️ Tecnologías

- Spring Boot
- Spring Data JPA
- H2 Database (en memoria)
- Eureka Client
