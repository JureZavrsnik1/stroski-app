# Stroški App

Spletna aplikacija za spremljanje osebnih stroškov: uporabnik zapisuje stroške, jih razvršča v kategorije ter jih išče, filtrira in pregleduje po straneh.

## Kazalo
1. [Vizija](#vizija)
2. [Opis rešitve](#opis-rešitve)
3. [Ključne funkcionalnosti](#ključne-funkcionalnosti)
4. [Obseg rešitve in omejitve](#obseg-rešitve-in-omejitve)
5. [Besednjak](#besednjak)
6. [O projektu](#o-projektu)
7. [Uporabljene tehnologije](#uporabljene-tehnologije)
8. [Struktura projekta](#struktura-projekta)
9. [Navodila za nameščanje in zagon](#navodila-za-nameščanje-in-zagon)
10. [Navodila za razvijalce](#navodila-za-razvijalce)
11. [Standardi kodiranja](#standardi-kodiranja)


## Vizija

### Opis rešitve

Mnogi posamezniki, predvsem študenti in mladi, ki se šele osamosvajajo, nimajo jasnega pregleda nad tem, koliko denarja porabijo in za kaj. Stroški se razpršijo med gotovino, kartico in spletne nakupe, zato je ob koncu meseca težko ugotoviti, kam je denar izginil. Obstoječe rešitve so pogosto preveč zapletene, plačljive ali vezane na banko.

Naša aplikacija **Stroški App** je preprosta spletna aplikacija za beleženje in pregled osebnih stroškov. Njen namen je uporabniku omogočiti, da v nekaj sekundah zabeleži strošek in kadarkoli dobi jasen pregled nad svojo porabo. Tako bo lažje prepoznal, kje porabi preveč, in bolj premišljeno upravljal svoj denar.

Aplikacija je namenjena predvsem **študentom in posameznikom**, ki želijo brez večjega truda spremljati svojo osebno porabo, ne potrebujejo pa zahtevnih računovodskih orodij.

Aplikacija je sestavljena iz dveh delov:

- **zaledni del (backend)** v ogrodju Spring Boot, ki skrbi za poslovno logiko in shranjevanje podatkov v podatkovno bazo PostgreSQL ter ponuja REST API (dokumentiran s Swagger/OpenAPI),
- **čelni del (frontend)** v knjižnici React, preko katerega uporabnik v brskalniku dodaja, ureja in pregleduje svoje stroške.

Vsak strošek ima naziv, znesek, datum, kategorijo in neobvezno opombo. Uporabnik stroške razvršča v kategorije (npr. hrana, prevoz, stanovanje, zabava), kar mu omogoča pregled porabe po področjih.

### Ključne funkcionalnosti

- **Dodajanje stroška** – hiter vnos novega stroška z nazivom, zneskom, datumom, kategorijo in opombo.
- **Urejanje in brisanje stroškov** – popravek napačno vnesenega ali odstranitev nepotrebnega stroška.
- **Seznam stroškov** – pregleden prikaz vseh zabeleženih stroškov.
- **Iskanje in filtriranje** – iskanje po nazivu ter filtriranje po kategoriji in časovnem obdobju.
- **Razvrščanje in paginacija** – razvrščanje po datumu, znesku ali nazivu ter prikaz po straneh, kar ohranja preglednost tudi pri večjem številu vnosov.
- **Upravljanje kategorij** – dodajanje in urejanje kategorij stroškov.
- **Pregled porabe** – prikaz skupne porabe v izbranem obdobju in po kategorijah.

### Obseg rešitve in omejitve

**Obseg rešitve:**

- Aplikacija je namenjena beleženju **osebnih** stroškov enega uporabnika.
- Deluje kot spletna aplikacija v brskalniku.
- Vsi podatki se vnašajo ročno.
- Zajema osnovno upravljanje stroškov in kategorij ter enostavne preglede porabe.

**Omejitve:**

- Aplikacija **ni povezana z bančnimi računi**, zato se transakcije ne uvažajo samodejno.
- Podpira le eno valuto (EUR), pretvorbe med valutami ni.
- Ne podpira deljenja stroškov med več uporabniki (npr. skupni stroški sostanovalcev).
- Ne vsebuje naprednih finančnih funkcij, kot so napovedi porabe, investicije ali davčni izračuni.
- Ni samostojne mobilne aplikacije – dostop je mogoč le prek brskalnika.
- Ker gre za študijski projekt, aplikacija ni namenjena produkcijski uporabi z večjim številom uporabnikov.

## Besednjak

| Izraz | Razlaga |
|---|---|
| **Strošek** | Posamezen zabeležen izdatek uporabnika. Vsebuje naziv, znesek, datum, kategorijo in neobvezno opombo. |
| **Znesek** | Vrednost stroška v evrih (EUR). |
| **Datum stroška** | Dan, ko je bil strošek nastal (ne nujno dan, ko je bil vnesen v aplikacijo). |
| **Opomba** | Neobvezno dodatno besedilo, s katerim uporabnik podrobneje opiše strošek. |
| **Kategorija** | Skupina, v katero uporabnik razvrsti strošek glede na področje porabe (npr. hrana, prevoz, stanovanje, zabava). Vsak strošek pripada natanko eni kategoriji. |
| **Obdobje** | Časovni razpon (od–do), za katerega uporabnik želi videti stroške ali porabo, npr. tekoči mesec. |
| **Poraba** | Seštevek zneskov vseh stroškov v izbranem obdobju. |
| **Poraba po kategorijah** | Prikaz porabe, razdeljene glede na kategorije, iz katerega je razvidno, na katerem področju uporabnik porabi največ. |
| **Pregled porabe** | Del aplikacije, kjer uporabnik vidi skupno porabo in porabo po kategorijah za izbrano obdobje. |

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