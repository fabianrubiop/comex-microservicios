# 🚀 Sistema de Gestión Aduanera Multi-Módulo (Comex) - Rama EA3

Este repositorio contiene la arquitectura distribuida de microservicios para la plataforma de Comercio Exterior (Comex). El ecosistema ha sido diseñado y optimizado bajo un enfoque ágil multi-módulo enfocado en la modularidad, alta cohesión y bajo acoplamiento.

> 📢 **Nota Importante para la Evaluación:** Esta rama (`ea3`) contiene la versión de entrega final correspondiente a la **Evaluación de Aprendizaje 3 (Documentación, Pruebas Unitarias e Infraestructura)**. El historial e integraciones implementados superan las configuraciones base de las entregas anteriores.

---

## 🛠️ Stack Tecnológico Utilizado
* **Lenguaje de Programación:** Java 21 
* **Framework Base:** Spring Boot 3.x / Spring Cloud
* **Herramienta de Construcción:** Maven (Estructura Multimódulo)
* **Entorno de Desarrollo:** IntelliJ IDEA 2025.2.6.1 (Community Edition)
* **Orquestación y Despliegue:** Docker Desktop / Docker Hub / Docker Compose

---

## 🏗️ Arquitectura de Software Implementada

El proyecto está compuesto por los siguientes módulos integrados:

### Módulos de Infraestructura
* **`eureka-server`:** Servidor centralizado de descubrimiento y registro de servicios (Spring Cloud Eureka).
* **`api-gateway`:** Puerta de enlace única para el enrutamiento de peticiones externas, perfiles de seguridad y unificación de puertos.

### Microservicios de Negocio
* `carga-ms` | `banco-ms` | `clasificacion-ms` | `documentos-ms` | `notificacion-ms` | `pagos-ms` | `riesgo-ms`

---

## 🧪 Estrategia de Calidad y Pruebas (EA3)

Cumpliendo rigurosamente con los lineamientos del Plan de Pruebas institucional, se integraron los siguientes componentes de automatización:

1.  **Pruebas Unitarias Aisladas (JUnit 5 + Mockito):** Lógica de negocio validada en aislamiento total mediante el uso de dobles de prueba (`@Mock`) para evitar conexiones volátiles a bases de datos en fase de test.
2.  **Generación de Datos Aleatorios (DataFaker):** Inyección de datos dinámicos realistas en tiempo de ejecución para robustecer las pruebas funcionales.
3.  **Auditoría de Cobertura (JaCoCo):** Integración automatizada vía `jacoco-maven-plugin` asegurando un estándar de cobertura superior al **80%** de las líneas de código de negocio.
4.  **Documentación Interactiva (Swagger / OpenAPI 3):** Exposición de contratos y esquemas de datos de los endpoints directo en interfaz web accesible localmente.
5.  **Navegación Hipermedia (HateOas):** Respuestas API enriquecidas con enlaces de auto-descubrimiento REST nativos.

---

## 🚀 Instrucciones de Ejecución Local

### Prerequisitos
* Tener instalado Docker Desktop y Git.
* Java 21 configurado en las variables de entorno del sistema.

### Pasos para Levantar el Ecosistema Completo:

1.  **Clonar el repositorio y moverse a la rama correcta:**
    ```bash
    git clone [https://github.com/fabianrubiop/comex-microservicios.git](https://github.com/fabianrubiop/comex-microservicios.git)
    cd comex-microservicios
    git checkout ea3
    ```

2.  **Limpiar compilaciones antiguas y empaquetar el código fuente:**
    ```bash
    ./mvnw clean package -DskipTests
    ```

3.  **Correr toda la suite de pruebas unitarias y automatizar la generación de reportes JaCoCo/Surefire:**
    ```bash
    ./mvnw test
    ```
    *Los reportes locales en formato HTML, XML y TXT se generarán automáticamente dentro de la ruta `target/site/jacoco/index.html` de cada microservicio.*

4.  **Levantar la infraestructura física en contenedores:**
    ```bash
    docker-compose up -d --build
    ```

---
✒️ **Desarrollado por:** Tomás Ossandón, Fabián Rubio, Estefania Ruiz - Duoc UC (2026).