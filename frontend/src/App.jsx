import { useState, useEffect } from "react";
import "./App.css";

const API = "http://localhost:8080/api";
const VELIKOST_STRANI = 5; // koliko stroškov je na eni strani

function App() {
  // --- podatki iz zaledja ---
  const [stroski, setStroski] = useState([]);       // stroški na trenutni strani
  const [kategorije, setKategorije] = useState([]); // vse kategorije
  const [steviloStrani, setSteviloStrani] = useState(0);

  // --- iskanje, filter, paginacija, razvrščanje ---
  const [iskanje, setIskanje] = useState("");
  const [filterKategorija, setFilterKategorija] = useState(""); // "" = vse kategorije
  const [stran, setStran] = useState(0);
  const [razvrsti, setRazvrsti] = useState("datum");
  const [smer, setSmer] = useState("desc");

  // --- obrazec in ostalo ---
  const prazenObrazec = { naziv: "", opomba: "", znesek: "", datum: "", kategorijaId: "" };
  const [obrazec, setObrazec] = useState(prazenObrazec);
  const [urejanjeId, setUrejanjeId] = useState(null);
  const [izbran, setIzbran] = useState(null);
  const [novaKategorija, setNovaKategorija] = useState("");
  const [napaka, setNapaka] = useState("");

  // Naloži stroške z iskanjem, filtrom, stranjo in razvrščanjem
  async function naloziStroske() {
    const parametri = new URLSearchParams({
      iskanje: iskanje,
      stran: stran,
      velikost: VELIKOST_STRANI,
      razvrsti: razvrsti,
      smer: smer,
    });
    // Kategorijo dodamo samo, če je izbrana
    if (filterKategorija !== "") {
      parametri.append("kategorijaId", filterKategorija);
    }

    const odgovor = await fetch(`${API}/strosek/iskanje?${parametri}`);
    const podatki = await odgovor.json();
    setStroski(podatki.content);
    setSteviloStrani(podatki.totalPages);
  }

  async function naloziKategorije() {
    const odgovor = await fetch(`${API}/kategorija`);
    setKategorije(await odgovor.json());
  }

  // Kategorije naložimo enkrat ob zagonu
  useEffect(() => {
    naloziKategorije();
  }, []);

  // Stroške naložimo ob zagonu in vsakič, ko se spremeni katerikoli parameter iskanja
  useEffect(() => {
    naloziStroske();
  }, [iskanje, filterKategorija, stran, razvrsti, smer]);

  // Ob spremembi iskanja ali filtra se vrnemo na prvo stran
  function spremeniIskanje(besedilo) {
    setIskanje(besedilo);
    setStran(0);
  }

  function spremeniFilter(idKategorije) {
    setFilterKategorija(idKategorije);
    setStran(0);
  }

  // Klik na glavo stolpca: isto polje obrne smer, drugo polje začne z naraščajočo
  function kliknjenoRazvrscanje(polje) {
    if (razvrsti === polje) {
      setSmer(smer === "asc" ? "desc" : "asc");
    } else {
      setRazvrsti(polje);
      setSmer("asc");
    }
    setStran(0);
  }

  function puscica(polje) {
    if (razvrsti !== polje) return "";
    return smer === "asc" ? " ▲" : " ▼";
  }

  function spremeniPolje(e) {
    setObrazec({ ...obrazec, [e.target.name]: e.target.value });
  }

  // Dodaj ali posodobi strošek
  async function shrani(e) {
    e.preventDefault();
    setNapaka("");

    const telo = {
      naziv: obrazec.naziv,
      opomba: obrazec.opomba,
      znesek: parseFloat(obrazec.znesek),
      datum: obrazec.datum,
      kategorija: { id: parseInt(obrazec.kategorijaId) },
    };

    const naslov = urejanjeId ? `${API}/strosek/${urejanjeId}` : `${API}/strosek`;
    const odgovor = await fetch(naslov, {
      method: urejanjeId ? "PUT" : "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(telo),
    });

    if (!odgovor.ok) {
      setNapaka("Shranjevanje ni uspelo. Preveri vnos.");
      return;
    }
    preklici();
    naloziStroske();
  }

  function zacniUrejanje(strosek) {
    setUrejanjeId(strosek.id);
    setObrazec({
      naziv: strosek.naziv,
      opomba: strosek.opomba || "",
      znesek: strosek.znesek,
      datum: strosek.datum,
      kategorijaId: strosek.kategorija ? strosek.kategorija.id : "",
    });
  }

  function preklici() {
    setUrejanjeId(null);
    setObrazec(prazenObrazec);
  }

  async function izbrisi(id) {
    await fetch(`${API}/strosek/${id}`, { method: "DELETE" });
    if (izbran && izbran.id === id) setIzbran(null);
    naloziStroske();
  }

  async function pokaziPodrobnosti(id) {
    const odgovor = await fetch(`${API}/strosek/${id}`);
    setIzbran(await odgovor.json());
  }

  async function dodajKategorijo(e) {
    e.preventDefault();
    if (!novaKategorija.trim()) return;
    await fetch(`${API}/kategorija`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ naziv: novaKategorija }),
    });
    setNovaKategorija("");
    naloziKategorije();
  }

  return (
    <div className="app">
      <h1>Stroški</h1>
      {napaka && <p className="napaka">{napaka}</p>}

      {/* Dodajanje kategorije */}
      <form onSubmit={dodajKategorijo}>
        <input
          placeholder="Nova kategorija"
          value={novaKategorija}
          onChange={(e) => setNovaKategorija(e.target.value)}
        />
        <button type="submit">Dodaj kategorijo</button>
      </form>

      {/* Obrazec za strošek */}
      <form onSubmit={shrani}>
        <h2>{urejanjeId ? "Uredi strošek" : "Nov strošek"}</h2>
        <input name="naziv" placeholder="Naziv" value={obrazec.naziv} onChange={spremeniPolje} required />
        <input name="opomba" placeholder="Opomba" value={obrazec.opomba} onChange={spremeniPolje} />
        <input name="znesek" type="number" step="0.01" placeholder="Znesek" value={obrazec.znesek} onChange={spremeniPolje} required />
        <input name="datum" type="date" value={obrazec.datum} onChange={spremeniPolje} required />
        <select name="kategorijaId" value={obrazec.kategorijaId} onChange={spremeniPolje} required>
          <option value="">Izberi kategorijo</option>
          {kategorije.map((k) => (
            <option key={k.id} value={k.id}>{k.naziv}</option>
          ))}
        </select>
        <button type="submit">{urejanjeId ? "Posodobi" : "Dodaj"}</button>
        {urejanjeId && <button type="button" onClick={preklici}>Prekliči</button>}
      </form>

      {/* Iskanje in filter */}
      <div className="iskanje">
        <input
          placeholder="Išči po nazivu..."
          value={iskanje}
          onChange={(e) => spremeniIskanje(e.target.value)}
        />
        <select value={filterKategorija} onChange={(e) => spremeniFilter(e.target.value)}>
          <option value="">Vse kategorije</option>
          {kategorije.map((k) => (
            <option key={k.id} value={k.id}>{k.naziv}</option>
          ))}
        </select>
      </div>

      {/* Tabela z razvrščanjem po klikih na glavo */}
      <table>
        <thead>
          <tr>
            <th onClick={() => kliknjenoRazvrscanje("naziv")}>Naziv{puscica("naziv")}</th>
            <th onClick={() => kliknjenoRazvrscanje("znesek")}>Znesek{puscica("znesek")}</th>
            <th onClick={() => kliknjenoRazvrscanje("datum")}>Datum{puscica("datum")}</th>
            <th>Kategorija</th>
            <th>Akcije</th>
          </tr>
        </thead>
        <tbody>
          {stroski.map((s) => (
            <tr key={s.id}>
              <td>{s.naziv}</td>
              <td>{s.znesek} €</td>
              <td>{s.datum}</td>
              <td>{s.kategorija ? s.kategorija.naziv : "-"}</td>
              <td>
                <button onClick={() => pokaziPodrobnosti(s.id)}>Podrobnosti</button>
                <button onClick={() => zacniUrejanje(s)}>Uredi</button>
                <button onClick={() => izbrisi(s.id)}>Izbriši</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {stroski.length === 0 && <p>Ni zadetkov.</p>}

      {/* Paginacija */}
      <div className="paginacija">
        <button onClick={() => setStran(stran - 1)} disabled={stran === 0}>
          Prejšnja
        </button>
        <span> Stran {steviloStrani === 0 ? 0 : stran + 1} od {steviloStrani} </span>
        <button onClick={() => setStran(stran + 1)} disabled={stran + 1 >= steviloStrani}>
          Naslednja
        </button>
      </div>

      {/* Podrobnosti */}
      {izbran && (
        <div className="podrobnosti">
          <h3>{izbran.naziv}</h3>
          <p>Opomba: {izbran.opomba}</p>
          <p>Znesek: {izbran.znesek} €</p>
          <p>Datum: {izbran.datum}</p>
          <p>Kategorija: {izbran.kategorija ? izbran.kategorija.naziv : "-"}</p>
          <button onClick={() => setIzbran(null)}>Zapri</button>
        </div>
      )}
    </div>
  );
}

export default App;