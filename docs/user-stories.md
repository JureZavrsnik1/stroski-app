# Uporabniške zgodbe

Funkcionalnosti: Pregled porabe po kategorijah, Izvoz stroškov v CSV

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