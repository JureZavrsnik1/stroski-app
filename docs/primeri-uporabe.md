# Podroben opis primerov uporabe

V nadaljevanju so opisani primeri uporabe, ki v aplikaciji še niso implementirani:

- Registracija
- Prijava
- Upravljanje kategorij
- Pregled porabe po kategorijah

## PU1: Registracija

| **Primer uporabe:** Registracija &nbsp;&nbsp;&nbsp; **ID:** PU1 |
|---|
| **Cilj:** Nov uporabnik želi ustvariti uporabniški račun, da bo lahko beležil in pregledoval svoje stroške. |
| **Akterji:** Uporabnik (neregistriran), Sistem |
| **Predpogoji:**<br>• Uporabnik še nima uporabniškega računa.<br>• Uporabnik ni prijavljen. |
| **Stanje sistema po PU:** Stanje se spremeni – v podatkovni bazi je shranjen nov uporabniški račun z vlogo »uporabnik«. |
| **Scenarij:**<br>1. Uporabnik na začetni strani izbere možnost »Registracija«.<br>2. Sistem prikaže obrazec za registracijo (uporabniško ime, e-poštni naslov, geslo, ponovitev gesla).<br>3. Uporabnik izpolni obrazec in ga potrdi.<br>4. Sistem preveri, ali so vsi podatki izpolnjeni in veljavni.<br>5. Sistem preveri, ali uporabniško ime in e-poštni naslov še nista v uporabi.<br>6. Sistem shrani nov uporabniški račun (geslo shrani zgoščeno).<br>7. Sistem prikaže sporočilo o uspešni registraciji in uporabnika preusmeri na stran za prijavo. |
| **Alternativni tokovi:**<br>3a. Uporabnik prekliče registracijo: sistem ga vrne na začetno stran, račun se ne ustvari.<br>4a. Podatki niso veljavni (prazno polje, neveljaven e-poštni naslov, prekratko geslo, gesli se ne ujemata): sistem ob polju prikaže opis napake, uporabnik podatke popravi in nadaljuje pri koraku 3.<br>5a. Uporabniško ime ali e-poštni naslov je že v uporabi: sistem prikaže opozorilo, uporabnik vnese drug podatek in nadaljuje pri koraku 3. |
| **Izjeme:**<br>• Napaka pri shranjevanju v podatkovno bazo: sistem prikaže sporočilo »Registracija trenutno ni mogoča, poskusite znova kasneje«, račun se ne ustvari.<br>• Strežnik ni dosegljiv: aplikacija prikaže sporočilo o napaki povezave, vneseni podatki v obrazcu ostanejo. |

## PU2: Prijava

| **Primer uporabe:** Prijava &nbsp;&nbsp;&nbsp; **ID:** PU2 |
|---|
| **Cilj:** Uporabnik ali administrator se želi prijaviti v aplikacijo, da dostopa do svojih podatkov in funkcionalnosti. |
| **Akterji:** Uporabnik, Administrator, Sistem |
| **Predpogoji:**<br>• Akter ima ustvarjen uporabniški račun.<br>• Akter ni prijavljen. |
| **Stanje sistema po PU:** Podatki v bazi se ne spremenijo. Sistem ustvari prijavno sejo (žeton), s katero je akter prijavljen. |
| **Scenarij:**<br>1. Akter izbere možnost »Prijava«.<br>2. Sistem prikaže obrazec za prijavo (uporabniško ime ali e-pošta, geslo).<br>3. Akter vnese podatke in potrdi prijavo.<br>4. Sistem preveri, ali račun obstaja in ali je geslo pravilno.<br>5. Sistem ustvari prijavno sejo.<br>6. Sistem akterja preusmeri na glavno stran: uporabnika na seznam stroškov, administratorja na upravljanje kategorij. |
| **Alternativni tokovi:**<br>3a. Akter pusti prazno polje: sistem opozori, da je polje obvezno, in ne pošlje zahteve.<br>4a. Uporabniško ime ali geslo ni pravilno: sistem prikaže sporočilo »Napačno uporabniško ime ali geslo« (ne razkrije, kateri podatek je napačen), akter nadaljuje pri koraku 3. |
| **Izjeme:**<br>• Večkratna neuspešna prijava (npr. 5-krat zapored): sistem začasno onemogoči prijavo za ta račun in prikaže ustrezno sporočilo.<br>• Strežnik ali podatkovna baza nista dosegljiva: sistem prikaže sporočilo, da prijava trenutno ni mogoča. |

## PU3: Upravljanje kategorij

| **Primer uporabe:** Upravljanje kategorij &nbsp;&nbsp;&nbsp; **ID:** PU3 |
|---|
| **Cilj:** Administrator želi dodajati, urejati in brisati kategorije stroškov, ki so na voljo uporabnikom. |
| **Akterji:** Administrator, Sistem |
| **Predpogoji:**<br>• Administrator je prijavljen (PU2).<br>• Prijavljeni akter ima vlogo »administrator«. |
| **Stanje sistema po PU:** Stanje se spremeni – v podatkovni bazi je dodana nova kategorija ali pa je obstoječa kategorija spremenjena oziroma izbrisana. |
| **Scenarij (dodajanje kategorije):**<br>1. Administrator odpre stran »Kategorije«.<br>2. Sistem prikaže seznam obstoječih kategorij.<br>3. Administrator izbere »Dodaj kategorijo«.<br>4. Sistem prikaže obrazec z nazivom kategorije.<br>5. Administrator vnese naziv in potrdi.<br>6. Sistem preveri, da naziv ni prazen in da kategorija s tem nazivom še ne obstaja.<br>7. Sistem shrani kategorijo in osveži seznam. |
| **Alternativni tokovi:**<br>3a. Urejanje kategorije: administrator pri kategoriji izbere »Uredi«, sistem prikaže obrazec z obstoječim nazivom, administrator ga spremeni in potrdi, sistem nadaljuje pri koraku 6.<br>3b. Brisanje kategorije: administrator pri kategoriji izbere »Izbriši«, sistem zahteva potrditev, administrator potrdi in sistem kategorijo izbriše.<br>&nbsp;&nbsp;&nbsp;3b1. Kategorija je uporabljena pri vsaj enem strošku: sistem kategorije ne izbriše in prikaže opozorilo, da je kategorija v uporabi.<br>6a. Naziv je prazen ali kategorija že obstaja: sistem prikaže opozorilo, administrator popravi naziv. |
| **Izjeme:**<br>• Akter brez vloge administratorja poskuša dostopati do strani: sistem dostop zavrne (napaka 403) in ga preusmeri na glavno stran.<br>• Napaka pri shranjevanju v bazo: sistem prikaže sporočilo o napaki, sprememba se ne shrani. |

## PU4: Pregled porabe po kategorijah

| **Primer uporabe:** Pregled porabe po kategorijah &nbsp;&nbsp;&nbsp; **ID:** PU4 |
|---|
| **Cilj:** Uporabnik želi videti, koliko je skupaj porabil v izbranem obdobju in koliko na posameznem področju (kategoriji). |
| **Akterji:** Uporabnik, Sistem |
| **Predpogoji:** Uporabnik je prijavljen (PU2). |
| **Stanje sistema po PU:** Stanje se ne spremeni – sistem podatke le prebere in prikaže. |
| **Scenarij:**<br>1. Uporabnik odpre stran »Pregled porabe«.<br>2. Sistem privzeto izbere tekoči mesec kot obdobje.<br>3. Uporabnik po želji izbere drugo obdobje (datum od–do) in potrdi.<br>4. Sistem pridobi vse stroške uporabnika v izbranem obdobju.<br>5. Sistem izračuna skupno porabo in porabo po posameznih kategorijah.<br>6. Sistem prikaže skupni znesek ter porabo po kategorijah v tabeli in grafu, razvrščeno od največje do najmanjše. |
| **Alternativni tokovi:**<br>3a. Datum »od« je kasnejši od datuma »do«: sistem prikaže opozorilo in ne izvede izračuna, uporabnik popravi obdobje.<br>4a. V izbranem obdobju ni nobenega stroška: sistem prikaže skupno porabo 0,00 € in sporočilo, da v obdobju ni stroškov. |
| **Izjeme:**<br>• Napaka pri branju podatkov iz baze: sistem prikaže sporočilo, da pregleda trenutno ni mogoče prikazati.<br>• Prijavna seja je potekla: sistem uporabnika preusmeri na prijavo (PU2). |