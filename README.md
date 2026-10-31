# Stroški App

Spletna aplikacija za spremljanje osebnih stroškov: uporabnik zapisuje stroške, jih razvršča v kategorije ter jih išče, filtrira in pregleduje po straneh.

## Kazalo
1. [O projektu](#o-projektu)
2. [Uporabljene tehnologije](#uporabljene-tehnologije)
3. [Struktura projekta](#struktura-projekta)
4. [Navodila za nameščanje in zagon](#navodila-za-nameščanje-in-zagon)
5. [Navodila za razvijalce](#navodila-za-razvijalce)
6. [Standardi kodiranja](#standardi-kodiranja)

## O projektu

Stroški App je namenjena posameznikom, ki želijo imeti pregled nad tem, koliko denarja porabijo in za kaj. Sestavljata jo zaledje (REST API) in ločen uporabniški vmesnik.

**Funkcionalnosti:**
- dodajanje, pregled, urejanje in brisanje stroškov (CRUD),
- dodajanje kategorij in dodeljevanje kategorije stroškom,
- iskanje stroškov po nazivu in filtriranje po kategoriji,
- paginacija (5 zapisov na stran) in razvrščanje po nazivu, znesku in datumu,
- dokumentacija API-ja v Swaggerju.

## Uporabljene tehnologije

### Zaledje (backend)

| Tehnologija | Verzija |
|---|---|
| Java (JDK) | 25 |
| Spring Boot | 4.1.1 |
| Spring Data JPA (Hibernate) | prinese Spring Boot 4.1.1 |
| Spring Web MVC | prinese Spring Boot 4.1.1 |
| PostgreSQL | 18 |
| PostgreSQL JDBC gonilnik | prinese Spring Boot 4.1.1 |
| springdoc-openapi (Swagger UI) | 3.0.3 |
| Maven (Maven Wrapper, priložen v `backend/`) | prenese se samodejno |

### Frontend

| Tehnologija | Verzija |
|---|---|
| Node.js | 24.14.1 |
| npm | 11.11.0 |
| React | 19.2.8 |
| Vite | 8.3.0 |
| ESLint | 10.10.0 |

## Struktura projekta

```
stroski-app/
├── backend/                         Spring Boot zaledje
│   ├── pom.xml                      odvisnosti in nastavitve gradnje
│   ├── mvnw, mvnw.cmd               Maven Wrapper (zagon brez nameščenega Mavena)
│   └── src/main/
│       ├── java/jar/
│       │   ├── controller/          REST krmilniki (StrosekController, KategorijaController)
│       │   ├── model/               entitete (Strosek, Kategorija)
│       │   ├── repository/          dostop do baze (JpaRepository, poizvedba za iskanje)
│       │   └── CorsConfig.java      dovoli zahteve s frontenda (localhost:5173)
│       └── resources/
│           └── application.properties   povezava z bazo in nastavitve
├── frontend/                        React uporabniški vmesnik
│   ├── package.json                 odvisnosti in skripte
│   └── src/
│       ├── App.jsx                  glavna komponenta (obrazec, iskanje, tabela, paginacija)
│       └── App.css                  stili
├── docs/                            dokumentacija in posnetki zaslona
├── postman/                         Postman zbirka za testiranje API-ja
├── .gitignore
└── README.md
```

## Navodila za nameščanje in zagon

### Predpogoji

- **Git**
- **Java JDK 25** (preveri z `java -version`)
- **Node.js 24** z npm (preveri z `node -v`)
- **PostgreSQL 18** (priporočen je tudi pgAdmin)

Maven ni potreben, ker projekt vsebuje Maven Wrapper.

### 1. Prenos projekta

```bash
git clone https://github.com/JureZavrsnik1/stroski-app.git
cd stroski-app
```

### 2. Vzpostavitev baze

Ustvari prazno bazo z imenom `stroski` (v pgAdminu ali v `psql`):

```sql
CREATE DATABASE stroski;
```

Tabele ustvari Hibernate sam ob prvem zagonu zaledja (`spring.jpa.hibernate.ddl-auto=update`).

### 3. Okoljske spremenljivke

Geslo za bazo ni v kodi. Zaledje ga prebere iz okoljske spremenljivke `DB_PASSWORD`. Nastavitve baze so v `backend/src/main/resources/application.properties` (baza `stroski`, uporabnik `postgres`, port `5432`).

**Windows (PowerShell):**
```powershell
setx DB_PASSWORD "tvoje_geslo"
```
Po nastavitvi zapri in znova odpri terminal (ali VS Code).

**Linux / macOS:**
```bash
export DB_PASSWORD="tvoje_geslo"
```

### 4. Zagon zaledja

```bash
cd backend
.\mvnw.cmd spring-boot:run      # Windows
./mvnw spring-boot:run          # Linux / macOS
```

- Zaledje: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### 5. Zagon frontenda

V drugem terminalu:

```bash
cd frontend
npm install
npm run dev
```

Aplikacija je na http://localhost:5173. Zaledje mora teči hkrati. Frontend mora biti na portu 5173, ker zaledje (CORS) dovoli samo ta naslov.

## Navodila za razvijalce

- Veja `main` vedno vsebuje delujočo kodo. Neposredno vanjo ne commitamo.
- Za vsako novo funkcionalnost ali popravek ustvari vejo (`feature/ime` ali `fix/ime`):
  ```bash
  git checkout -b feature/ime-funkcionalnosti
  ```
- Commite piši kratko, v povedni obliki, npr. `Dodaj iskanje po nazivu`.
- Pred pushem preveri, da se zaledje in frontend zaženeta.
- Vejo pošlji na GitHub in odpri **Pull Request** v `main`. Veja se združi po pregledu.
- V repozitorij ne dodajaj `node_modules/`, `target/`, gesel ali `.env` datotek.

## Standardi kodiranja

- **Java:** razredi `PascalCase`, metode in spremenljivke `camelCase`, paketi male črke. Razredi so razporejeni v pakete `controller`, `model`, `repository`. REST naslovi so oblike `/api/<vir>`, odgovori so JSON.
- **React:** komponente `PascalCase`, funkcije in spremenljivke `camelCase`. Uporabljamo funkcijske komponente s `useState` in `useEffect`. Kodo preverja ESLint (`npm run lint`).
- **Splošno:** jasna imena in kratki komentarji pri pomembnih delih kode. Zamik 4 presledki (Java), 2 presledka (JS, JSX, CSS).