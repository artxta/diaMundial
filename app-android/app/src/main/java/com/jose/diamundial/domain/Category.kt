package com.jose.diamundial.domain

enum class Category(val key: String) {
    HEALTH("health"),
    EDUCATION("education"),
    ENVIRONMENT("environment"),
    CULTURE("culture"),
    SCIENCE_TECH("science_tech");

    companion object {
        fun fromKey(key: String): Category? = entries.find { it.key == key }

        private val healthKeywords = listOf(
            "salud", "health", "omedical", "omental", "enfermedad", "disease",
            "tuberculosis", "paludismo", "malaria", "vih", "hiv", "sida", "aids",
            "cancer", "cáncer", "vacuna", "vaccin", "inmunización", "immunization",
            "mental", "nutrición", "nutrition", "alimentación", "food", "legumbres",
            "alimentos", "sanidad", "higiene", "hygiene", "hospital", "médic",
            "farmacéut", "pharmaceut", "odontolog", "dental", "sangre", "blood",
            "donación", "donat", "chagas", "ebola", "covid", "pandemia", "epidemia",
            "braille", "discapacidad", "disability", "autismo", "autism", "down",
            "lepra", "leprosy", "tracoma", "hepatitis", "tropical", "envejecimiento",
            "enfermedades", "primeros auxilios", "first aid", "seguridad alimentaria",
            "food safety", "sanidad vegetal", "plant health", "bienestar", "wellbeing",
            "mujer y la niña en la ciencia"
        )

        private val educationKeywords = listOf(
            "educación", "education", "escuela", "school", "universidad", "university",
            "alfabetización", "literacy", "leer", "read", "libro", "book", "poesía",
            "poetry", "lengua materna", "mother tongue", "idioma", "language",
            "enseñanza", "teaching", "aprendizaje", "learning", "estudiante", "student",
            "docente", "teacher", "formación", "training", "beca", "scholarship",
            "conocimiento", "knowledge", "investigación", "research"
        )

        private val environmentKeywords = listOf(
            "medio ambiente", "environment", "clima", "climate", "bosque", "forest",
            "agua", "water", "océano", "ocean", "mar", "sea", "mundo marino", "marine",
            "humedal", "wetland", "biodiversidad", "biodiversity", "especie", "species",
            "animal", "ave", "bird", "fauna", "flora", "selva", "jungle", "tierra",
            "earth", "ecosistema", "ecosystem", "contaminación", "pollution",
            "reciclaje", "recycling", "desechos", "waste", "residuos",
            "energía limpia", "clean energy", "renovable", "renewable", "solar",
            "recursos naturales", "natural resources", "deforestación", "desertificación",
            "glaciar", "glacier", "nieve", "snow", "pastos marinos", "seagrass",
            "atún", "tuna", "migratoria", "migratory", "leopardo", "leopard",
            "vida silvestre", "wildlife", "naturaleza", "nature", "planeta", "planet",
            "amazonía", "amazon", "semaforo ecológico", "humedales", "suelo",
            "meteorolog", "agricultura", "agriculture", "ganadería", "livestock",
            "pesca", "fisheries", "ARGANES", "arganes"
        )

        private val cultureKeywords = listOf(
            "cultura", "culture", "arte", "art", "música", "music", "jazz", "dance",
            "danza", "cine", "cinema", "teatro", "theater", "literatura", "literature",
            "patrimonio", "heritage", "arqueolog", "museo", "museum", "folclore",
            "folkl", "tradiciones", "traditions", "identidad", "identity",
            "tolerancia", "tolerance", "paz", "peace", "convivencia", "coexistence",
            "fraternidad", "fraternity", "solidaridad", "solidarity", "derechos humanos",
            "human rights", "igualdad", "equality", "mujer", "woman", "niño", "child",
            "infancia", "childhood", "familia", "family", "diversidad", "diversity",
            "inclusión", "inclusion", "discriminación", "discrimination", "esclavitud",
            "slavery", "refugiado", "refugee", "migrante", "migrant", "indígena",
            "indigenous", "afrodescendiente", "afro", "novruz", "vesak",
            "cooperación", "cooperation", "diplomacia", "diplomacy", "multilateralismo",
            "jornada mundial del turismo", "turismo", "tourism", "deporte", "sport",
            "olímpic", "olympic", "felicidad", "happiness", "no violencia", "nonviolence",
            "humanitario", "humanitarian", "desarme", "disarmament", "paz",
            "lengua francesa", "french language", "lengua portuguesa", "portuguese language",
            "lengua china", "chinese language", "idioma español", "spanish language",
            "lengua inglesa", "english language", "jornada del derecho", "legal",
            "justicia social", "social justice", "trabajo", "labor", "sindical",
            "voluntariado", "volunteer", "ciudadanía", "citizenship", "democracia",
            "democracy", "libertad de prensa", "press freedom", "comunicación",
            "communication", "radio", "televisión", "internet", "digital",
            "propiedad intelectual", "intellectual property", "copyright", "patente",
            "coexistencia pacífica", "bienestar"
        )

        private val scienceTechKeywords = listOf(
            "ciencia", "science", "tecnología", "technology", "tic", "ict",
            "innovación", "innovation", "creatividad", "creativity", "espacio",
            "space", "astro", "nasa", "satélite", "satellite", "lunar", "mars",
            "exploración", "exploration", "matemátic", "mathematic", "física",
            "physics", "química", "chemistry", "biolog", "genétic", "genetic",
            "nanotecnolog", "nanotechnology", "inteligencia artificial", "artificial intelligence",
            "robótica", "robotics", "computación", "computing", "programación",
            "programming", "ciberseguridad", "cybersecurity", "datos", "data",
            "energía", "energy", "nuclear", "atóm", "atomic", "fusión", "fisión",
            "semiconductor", "electrónica", "electronics", "fotónica", "photonics",
            "superconductor", "biotecnolog", "biotechnology", "aerospacial",
            "aerospace", "vuelos espaciales", "space flight", "telescopio",
            "astronomía", "astronomy", "partículas", "particles", "experimento",
            "laboratorio", "laboratory", "invento", "invention", "patente", "patent",
            "braille"
        )

        fun classify(event: Event): Category {
            val text = "${event.title} ${event.description}".lowercase()

            val scores = mutableMapOf(
                HEALTH to 0,
                EDUCATION to 0,
                ENVIRONMENT to 0,
                CULTURE to 0,
                SCIENCE_TECH to 0
            )

            for (kw in healthKeywords) {
                if (text.contains(kw.lowercase())) scores[HEALTH] = scores[HEALTH]!! + 1
            }
            for (kw in educationKeywords) {
                if (text.contains(kw.lowercase())) scores[EDUCATION] = scores[EDUCATION]!! + 1
            }
            for (kw in environmentKeywords) {
                if (text.contains(kw.lowercase())) scores[ENVIRONMENT] = scores[ENVIRONMENT]!! + 1
            }
            for (kw in cultureKeywords) {
                if (text.contains(kw.lowercase())) scores[CULTURE] = scores[CULTURE]!! + 1
            }
            for (kw in scienceTechKeywords) {
                if (text.contains(kw.lowercase())) scores[SCIENCE_TECH] = scores[SCIENCE_TECH]!! + 1
            }

            val maxScore = scores.values.maxOrNull() ?: 0
            if (maxScore == 0) return CULTURE

            return scores.entries
                .filter { it.value == maxScore }
                .minByOrNull { it.key.ordinal }
                ?.key ?: CULTURE
        }
    }
}
