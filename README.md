# ⚙️ Compilador TINY - Extensión Procedural & Generación de Código

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![JFlex](https://img.shields.io/badge/Lexer-JFlex-blue?style=for-the-badge)](https://jflex.de/)
[![CUP](https://img.shields.io/badge/Parser-JavaCUP-red?style=for-the-badge)](http://www2.cs.tum.edu/projects/cup/)

Este proyecto consiste en un **compilador completo** para una variante robusta del lenguaje **TINY**. Partiendo de un núcleo básico de interpretación, el sistema fue expandido para soportar programación procedural, gestión de memoria avanzada y optimizaciones sintácticas, culminando en la generación de código objeto para la arquitectura **Tiny Machine (TM)**.

> **Nota de Desarrollo:** Este compilador fue evolucionado desde una base académica hacia una herramienta procedural funcional en un sprint de alto rendimiento, resolviendo desafíos críticos de lógica de programación y teoría de lenguajes.

---

## 🚀 Características Destacadas

### 1. Soporte Procedural Completo

A diferencia de las implementaciones básicas, este compilador permite:

* **Declaración de Funciones (`fun`):** Definición de subrutinas con pasaje de parámetros.
* **Gestión de Retorno:** Implementación de la sentencia `return` con manejo de tipos.
* **Ámbitos Jerárquicos (Scoping):** Gestión de variables locales y globales mediante una estructura de **pila de tablas de símbolos**, permitiendo la reutilización de identificadores en distintos contextos.

### 2. Estructuras de Datos Avanzadas

* **Arreglos (Vectores):** Soporte para declaración de arrays, asignación dinámica y acceso por índice, gestionando el desplazamiento de memoria relativo en la arquitectura TM.

### 3. Optimización Sintáctica (Lowering)

El compilador implementa una técnica de **reescritura de árbol (AST)** para la sentencia `FOR`. En lugar de complicar el generador de código, el nodo `FOR` se transforma internamente en una combinación lógica de `Asignación + If + Repeat`, garantizando eficiencia y minimalismo en el backend.

---

## 🏗️ Arquitectura del Sistema

El flujo de compilación sigue las fases clásicas de la ingeniería de software:

1. **Análisis Léxico (JFlex):** Tokenización de la gramática regular.
2. **Análisis Sintáctico (JavaCUP):** Construcción del Árbol de Sintaxis Abstracta (AST) basado en una gramática libre de contexto.
3. **Análisis Semántico:** Validación de tipos, declaración de variables y gestión de ámbitos.
4. **Generación de Código Objeto:** Traducción del AST a instrucciones ensamblador para la **Tiny Machine (TM)**, gestionando:
    * Marcos de pila (*Stack Frames*) para funciones.
    * Registros de CPU (`AC`, `AC1`, `PC`, `GP`).
    * Punteros de memoria temporales.

---

## 🛠️ Herramientas y Ejecución

El proyecto incluye un script de automatización en PowerShell (`Analist.ps1`) para regenerar el compilador tras cambios en la especificación.

### Requisitos

* JDK 8 o superior.
* Librerías JFlex y JavaCUP (incluidas en `/lib`).

### Instalación y Compilación

1. Clona el repositorio:

   ```bash
   git clone https://github.com/GregorioBrc/Compilador.git
   ```

2. Genera los analizadores (Lexer y Parser):

   ```powershell
   ./Analist.ps1
   ```

3. Ejecuta el compilador sobre un archivo fuente:

   ```bash
   java -cp "bin;lib/*" App ejemplo.tny
   ```

---

## 📂 Estructura de Archivos Clave

* **`analizadores/`**: Archivos `.flex` y `.cup` con la definición del lenguaje.
* **`nodosAST/`**: Implementación de la jerarquía de clases para el Árbol de Sintaxis Abstracta.
* **`TablaSimbolos.java`**: Lógica de gestión de ámbitos con pila de HashMaps.
* **`Generador.java`**: El backend encargado de la emisión de instrucciones TM.
* **`UtGen.java`**: Utilidades para el manejo de saltos y direcciones de memoria.

---

## 🎯 Desafíos Técnicos Superados

* **Recursividad de Funciones:** Ajuste fino de los desplazamientos de memoria para evitar colisiones entre variables locales de diferentes llamadas.
* **Transformación de Gramática:** Adaptar la gramática para permitir el uso de arreglos y funciones sin generar conflictos de reducción en el parser.
* **Persistencia de Ámbitos:** Implementar el cierre de ámbitos (*scope exit*) asegurando que la memoria se libere correctamente para futuras asignaciones.

---

**Desarrollado por:**   </br>
[Jose Gregorio Briceño Romero](https://gregoriobrc.github.io) </br>
[Francisco José Sanchez Zea](https://gregoriobrc.github.io) </br>
*Ingeniería Informática - Universidad Nacional Experimental del Táchira (UNET)*

---
