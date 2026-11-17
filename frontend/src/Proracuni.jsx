import { useState, useEffect } from "react";

const API = "http://localhost:8080/api";
const NAPAKA_ZNESEK = "Znesek proračuna mora biti večji od 0 €";

// Znesek v obliki 90,00 €
function evri(znesek) {
  return znesek.toLocaleString("sl-SI", { minimumFractionDigits: 2, maximumFractionDigits: 2 }) + " €";
}

// Tekoči mesec kot { leto, mesec }, mesec je 1–12
function tekociMesec() {
  const danes = new Date();
  return { leto: danes.getFullYear(), mesec: danes.getMonth() + 1 };
}

// Premik za "korak" mesecev naprej (+1) ali nazaj (-1), s prehodom čez leto
function premakniMesec({ leto, mesec }, korak) {
  const d = new Date(leto, mesec - 1 + korak, 1);
  return { leto: d.getFullYear(), mesec: d.getMonth() + 1 };
}

// Ime meseca, npr. "november 2026"
function imeMeseca({ leto, mesec }) {
  return new Date(leto, mesec - 1, 1).toLocaleDateString("sl-SI", { month: "long", year: "numeric" });
}

// Besedilo in razred CSS za stanje proračuna
function opisStanja(v) {
  switch (v.stanje) {
    case "OPOZORILO":
      return { besedilo: "Blizu meje proračuna", razred: "opozorilo" };
    case "PREKORACEN":
      return { besedilo: `Proračun prekoračen za ${evri(-v.preostanek)}`, razred: "prekoracen" };
    case "BREZ_PRORACUNA":
      return { besedilo: "Proračun ni nastavljen", razred: "brez" };
    default:
      return { besedilo: "V redu", razred: "vredu" };
  }
}

// US1: mesečni proračun po kategorijah in poraba glede nanj.
// Prop "osvezi" se spremeni, ko App doda/uredi/izbriše strošek ali kategorijo.
function Proracuni({ osvezi }) {
  const [mesec, setMesec] = useState(tekociMesec());
  const [pregled, setPregled] = useState([]);
  const [vnosi, setVnosi] = useState({}); // id kategorije -> vneseni znesek
  const [napaka, setNapaka] = useState("");
  const [osvezitev, setOsvezitev] = useState(0); // poveča se po shranjevanju ali odstranitvi

  // Pregled naložimo ob zagonu, ob menjavi meseca in po vsaki spremembi
  useEffect(() => {
    let aktivno = true; // prepreči nastavitev stanja iz zastarelega odgovora
    fetch(`${API}/proracun/pregled?leto=${mesec.leto}&mesec=${mesec.mesec}`)
      .then((odgovor) => (odgovor.ok ? odgovor.json() : Promise.reject()))
      .then((podatki) => {
        if (aktivno) {
          setPregled(podatki);
          setNapaka(""); // uspešno naloženo: stara napaka ni več aktualna
        }
      })
      .catch(() => {
        if (aktivno) setNapaka("Pregleda proračunov ni bilo mogoče naložiti.");
      });
    return () => {
      aktivno = false;
    };
  }, [mesec, osvezi, osvezitev]);

  async function shrani(kategorijaId) {
    setNapaka("");
    const vnos = vnosi[kategorijaId];
    const znesek = parseFloat(vnos);
    if (vnos === undefined || vnos === "" || isNaN(znesek) || znesek <= 0) {
      setNapaka(NAPAKA_ZNESEK);
      return;
    }
    const odgovor = await fetch(`${API}/proracun/${kategorijaId}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ znesek: znesek }),
    });
    if (!odgovor.ok) {
      const telo = await odgovor.json().catch(() => ({}));
      setNapaka(telo.napaka || "Shranjevanje proračuna ni uspelo.");
      return;
    }
    setVnosi({ ...vnosi, [kategorijaId]: "" });
    setOsvezitev(osvezitev + 1);
  }

  async function odstrani(kategorijaId, naziv) {
    if (!window.confirm(`Odstranim proračun za kategorijo »${naziv}«?`)) return;
    setNapaka("");
    await fetch(`${API}/proracun/${kategorijaId}`, { method: "DELETE" });
    setOsvezitev(osvezitev + 1);
  }

  return (
    <div className="proracuni">
      <h2>Proračuni</h2>
      <div className="izbira-meseca">
        <button onClick={() => setMesec(premakniMesec(mesec, -1))}>◀ Prejšnji</button>
        <strong>{imeMeseca(mesec)}</strong>
        <button onClick={() => setMesec(premakniMesec(mesec, 1))}>Naslednji ▶</button>
        <button className="sekundarni" onClick={() => setMesec(tekociMesec())}>Tekoči mesec</button>
      </div>
      {napaka && <p className="napaka">{napaka}</p>}

      <table>
        <thead>
          <tr>
            <th>Kategorija</th>
            <th>Poraba / proračun</th>
            <th>Stanje</th>
            <th>Nov znesek</th>
            <th>Akcije</th>
          </tr>
        </thead>
        <tbody>
          {pregled.map((v) => {
            const stanje = opisStanja(v);
            const imaProracun = v.proracun !== null;
            return (
              <tr key={v.kategorijaId}>
                <td>{v.kategorija}</td>
                <td>
                  {imaProracun ? `${evri(v.poraba)} / ${evri(v.proracun)}` : evri(v.poraba)}
                  {imaProracun && (
                    <>
                      <div className="vrstica-napredka">
                        <div
                          className={`napredek ${stanje.razred}`}
                          style={{ width: `${Math.min(v.odstotek, 100)}%` }}
                        />
                      </div>
                      <small>
                        {v.odstotek} %
                        {v.preostanek >= 0 && ` · Preostanek: ${evri(v.preostanek)}`}
                      </small>
                    </>
                  )}
                </td>
                <td className={`stanje ${stanje.razred}`}>{stanje.besedilo}</td>
                <td>
                  <input
                    type="number"
                    step="0.01"
                    placeholder="Znesek €"
                    value={vnosi[v.kategorijaId] ?? ""}
                    onChange={(e) => setVnosi({ ...vnosi, [v.kategorijaId]: e.target.value })}
                  />
                </td>
                <td>
                  <button onClick={() => shrani(v.kategorijaId)}>Shrani</button>
                  {imaProracun && (
                    <button onClick={() => odstrani(v.kategorijaId, v.kategorija)}>Odstrani proračun</button>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      {pregled.length === 0 && <p>Ni kategorij.</p>}
    </div>
  );
}

export default Proracuni;
