# Uporabniške zgodbe

Funkcionalnosti: Mesečni proračun in pregled porabe po kategorijah, Izvoz stroškov v CSV

## US1 – Mesečni proračun po kategorijah

**Zgodba:** Kot uporabnik želim za posamezne kategorije nastaviti mesečni proračun in videti, koliko sem v izbranem mesecu porabil glede nanj, da pravočasno opazim, kje bom prekoračil načrtovano porabo.

- **Odgovoren član:** Samuel Perovšek
- **Prioriteta:** visoka
- **Ocena zahtevnosti:** 5

### Sprejemni kriteriji

**AC1 – nastavitev proračuna (uspešen scenarij)**
- **Given:** uporabnik je v razdelku »Proračuni« in kategorija »Hrana« še nima proračuna.
- **When:** uporabnik za kategorijo »Hrana« vnese znesek 150,00 € in klikne »Shrani«.
- **Then:** ob kategoriji »Hrana« je prikazan proračun 150,00 €, ki ostane prikazan tudi po ponovnem nalaganju strani.

**AC2 – neveljaven znesek proračuna (scenarij napake)**
- **Given:** uporabnik je v razdelku »Proračuni«.
- **When:** uporabnik za kategorijo vnese znesek 0, negativen znesek ali pusti polje prazno in klikne »Shrani«.
- **Then:** proračun se ne shrani, prikaže se sporočilo »Znesek proračuna mora biti večji od 0 €« in prej shranjeni proračun ostane nespremenjen.

**AC3 – prikaz porabe pod mejo**
- **Given:** kategorija »Hrana« ima proračun 150,00 € in v tekočem mesecu ima uporabnik v tej kategoriji stroške v skupnem znesku 90,00 €.
- **When:** v razdelku »Proračuni« je izbran tekoči mesec.
- **Then:** ob kategoriji »Hrana« je prikazano »90,00 € / 150,00 €«, »60 %« in »Preostanek: 60,00 €«.

**AC4 – opozorilo ob približevanju meji**
- **Given:** kategorija »Hrana« ima proračun 150,00 € in v tekočem mesecu ima uporabnik v tej kategoriji stroške v skupnem znesku 125,00 € (83 %).
- **When:** v razdelku »Proračuni« je izbran tekoči mesec.
- **Then:** kategorija »Hrana« je označena z opozorilom »Blizu meje proračuna«.

**AC5 – prekoračen proračun**
- **Given:** kategorija »Hrana« ima proračun 150,00 € in v tekočem mesecu ima uporabnik v tej kategoriji stroške v skupnem znesku 170,00 €.
- **When:** v razdelku »Proračuni« je izbran tekoči mesec.
- **Then:** kategorija »Hrana« je označena kot »Proračun prekoračen za 20,00 €«.

**AC6 – kategorija brez proračuna**
- **Given:** kategorija »Prevoz« nima proračuna in v tekočem mesecu ima uporabnik v njej stroške v skupnem znesku 30,00 €.
- **When:** v razdelku »Proračuni« je izbran tekoči mesec.
- **Then:** ob kategoriji »Prevoz« je prikazano »30,00 €« in »Proračun ni nastavljen«, brez odstotka in opozorila.

**AC7 – upoštevajo se le stroški izbranega meseca**
- **Given:** kategorija »Hrana« ima proračun 150,00 €, v oktobru ima uporabnik v njej stroške 100,00 €, v novembru pa 40,00 €.
- **When:** uporabnik v razdelku »Proračuni« izbere november.
- **Then:** ob kategoriji »Hrana« je prikazano »40,00 € / 150,00 €«.

### Podrobnosti implementacije

**Poslovna pravila**
- Proračun je mesečni in velja za vsak mesec enako. Kategorija ima največ en proračun. Znesek je v EUR in večji od 0.
- Odstotek porabe = poraba v mesecu / proračun × 100, zaokrožen na celo število.
- Stanja: pod 80 % = v redu, od 80 % do vključno 100 % = »Blizu meje proračuna«, nad 100 % = »Proračun prekoračen za X €«, brez proračuna = »Proračun ni nastavljen«.
- Mesec obsega stroške z datumom stroška od prvega do zadnjega dne meseca (vključno), ne glede na datum vnosa. Privzeto je izbran tekoči mesec.

**Backend**
- Nova entiteta `Proracun` (`jar.model`): `id`, `kategorija` (`@OneToOne`, ena kategorija ima največ en proračun), `znesek` (`double`, enako kot pri `Strosek`).
- `ProracunRepository extends JpaRepository<Proracun, Long>` z metodo `findByKategorijaId(Long kategorijaId)`.
- `ProracunController` z endpointi:
  - `GET /api/proracun` – vsi proračuni,
  - `PUT /api/proracun/{kategorijaId}` s telesom `{ "znesek": 150.00 }` – ustvari ali posodobi proračun (404, če kategorija ne obstaja),
  - `DELETE /api/proracun/{kategorijaId}` – odstrani proračun,
  - `GET /api/proracun/pregled?leto=2026&mesec=11` – poraba glede na proračun za izbrani mesec.
- Znesek mora biti večji od 0 in mesec med 1 in 12, sicer endpoint vrne status 400 (Bad Request) s sporočilom napake.
- V `StrosekRepository` nova poizvedba, ki za obdobje vrne vsoto zneskov po kategorijah (`SUM(s.znesek) ... WHERE s.datum BETWEEN :od AND :do GROUP BY s.kategorija.id`).
- Izračun odstotka, preostanka in stanja (`V_REDU`, `OPOZORILO`, `PREKORACEN`, `BREZ_PRORACUNA`) v ločenem razredu, z enotnimi testi za meje 79 %, 80 %, 100 % in 101 %.
- Endpointi so dokumentirani v Swaggerju (`@Tag`, `@Operation`, `@ApiResponse`).

**Frontend**
- Nova komponenta `Proracuni.jsx`, vključena v `App.jsx` kot razdelek »Proračuni«.
- Izbirnik meseca (privzeto tekoči mesec) in tabela kategorij: poraba in proračun (npr. »90,00 € / 150,00 €«) ali »Proračun ni nastavljen«, vrstica napredka in odstotek.
- Barva glede na stanje (zelena, rumena, rdeča) in besedilo stanja, da stanje ni označeno samo z barvo.
- V vsaki vrstici vnosno polje za znesek ter gumba »Shrani« in »Odstrani proračun«. Preverjanje vnosa (prazno, ≤ 0) in prikaz napake.

**Opombe**
- Aplikacija še nima prijave uporabnikov, zato so proračuni skupni. Ko bo prijava dodana, se proračun veže na uporabnika.

## US2 – Izvoz stroškov v CSV

**Zgodba:** Kot uporabnik želim izvoziti svoje stroške v datoteko CSV, da jih lahko pregledam ali obdelam v preglednici.

- **Odgovoren član:** Jure Završnik
- **Prioriteta:** srednja
- **Ocena zahtevnosti:** 3

### Sprejemni kriteriji

**AC1 – izvoz vseh stroškov (uspešen scenarij)**
- **Given:** uporabnik ima zabeležene 3 stroške in ni nastavil nobenega filtra.
- **When:** uporabnik klikne gumb »Izvozi v CSV«.
- **Then:** sistem prenese datoteko `stroski.csv` z eno naslovno vrstico in 3 vrsticami podatkov (naziv, znesek, datum, kategorija).

**AC2 – izvoz upošteva filter**
- **Given:** uporabnik je v seznamu izbral kategorijo »Hrana« in prikazani sta 2 vrstici.
- **When:** uporabnik klikne gumb »Izvozi v CSV«.
- **Then:** datoteka vsebuje naslovno vrstico in samo ti 2 vrstici s kategorijo »Hrana«.

**AC3 – ni stroškov za izvoz (scenarij napake)**
- **Given:** uporabnik nima nobenega stroška, ki ustreza trenutnemu prikazu.
- **When:** uporabnik klikne gumb »Izvozi v CSV«.
- **Then:** sistem ne prenese datoteke in prikaže sporočilo »Ni stroškov za izvoz«.

### Podrobnosti implementacije

**Backend**
- V `StrosekController` dodamo endpoint `GET /api/strosek/izvoz` z istimi parametri filtra kot pri iskanju (`iskanje`, `kategorijaId`). Uporabi obstoječo poizvedbo `poisci` brez paginacije, tako izvoz vsebuje vse zadetke, ne le trenutne strani.
- Endpoint sestavi besedilo CSV: naslovna vrstica (`naziv;znesek;datum;kategorija`) in po ena vrstica za vsak strošek. Ločilo je podpičje, ker Excel v slovenskem jeziku privzeto uporablja to.
- Odgovor ima `Content-Type: text/csv; charset=UTF-8`, da se šumniki pravilno prikažejo.
- Če ni nobenega zadetka, endpoint vrne status 204 (No Content).

**Frontend**
- Dodamo gumb »Izvozi v CSV«, ki pošlje trenutno vrednost iskanja in izbrano kategorijo v endpoint `/api/strosek/izvoz`.
- Odgovor preberemo kot `blob`, ustvarimo začasen URL (`URL.createObjectURL`) in sprožimo prenos datoteke `stroski.csv`.
- Če je odgovor 204, prenosa ne sprožimo in prikažemo sporočilo »Ni stroškov za izvoz«.

**Opombe**
- Filter v izvozu je enak kot v seznamu, zato uporabnik izvozi natanko to, kar vidi, ne glede na število strani.
- Vrednosti, ki vsebujejo podpičje ali narekovaj (npr. v nazivu stroška), v CSV obdamo z narekovaji.