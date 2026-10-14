import { useEffect, useState } from 'react'
import './App.css'

// Naslov zaledja (Spring Boot)
const API = 'http://localhost:8080/api'

// Prazen obrazec za nov strošek
const prazenObrazec = { naziv: '', opomba: '', znesek: '', datum: '', kategorijaId: '' }

function App() {
  const [stroski, setStroski] = useState([])        // seznam vseh stroškov
  const [kategorije, setKategorije] = useState([])  // seznam kategorij (za izbirni seznam)
  const [obrazec, setObrazec] = useState(prazenObrazec)
  const [urejanjeId, setUrejanjeId] = useState(null) // ID stroška, ki ga urejamo (null = dodajamo novega)
  const [izbran, setIzbran] = useState(null)         // strošek, prikazan v podrobnostih
  const [novaKategorija, setNovaKategorija] = useState('')
  const [napaka, setNapaka] = useState('')

  // READ vsi: naloži stroške in kategorije iz zaledja
  async function naloziPodatke() {
    try {
      const [odgovorStroski, odgovorKategorije] = await Promise.all([
        fetch(`${API}/strosek`),
        fetch(`${API}/kategorija`),
      ])
      setStroski(await odgovorStroski.json())
      setKategorije(await odgovorKategorije.json())
      setNapaka('')
    } catch {
      setNapaka('Ni povezave z zaledjem. Preveri, ali Spring Boot teče na portu 8080.')
    }
  }

  // Ob prvem prikazu strani naloži podatke
  useEffect(() => {
    naloziPodatke()
  }, [])

  // Posodobi polje v obrazcu, ko uporabnik tipka
  function spremeniPolje(e) {
    setObrazec({ ...obrazec, [e.target.name]: e.target.value })
  }

  // CREATE ali UPDATE: odvisno od tega, ali urejamo obstoječ strošek
  async function shrani(e) {
    e.preventDefault()

    const telo = {
      naziv: obrazec.naziv,
      opomba: obrazec.opomba,
      znesek: parseFloat(obrazec.znesek),
      datum: obrazec.datum,
      kategorija: obrazec.kategorijaId ? { id: Number(obrazec.kategorijaId) } : null,
    }

    const url = urejanjeId ? `${API}/strosek/${urejanjeId}` : `${API}/strosek`
    const metoda = urejanjeId ? 'PUT' : 'POST'

    const odgovor = await fetch(url, {
      method: metoda,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(telo),
    })

    if (!odgovor.ok) {
      setNapaka('Shranjevanje ni uspelo.')
      return
    }

    setObrazec(prazenObrazec)
    setUrejanjeId(null)
    setNapaka('')
    naloziPodatke()
  }

  // Prenesi izbran strošek v obrazec za urejanje
  function zacniUrejanje(strosek) {
    setUrejanjeId(strosek.id)
    setObrazec({
      naziv: strosek.naziv ?? '',
      opomba: strosek.opomba ?? '',
      znesek: strosek.znesek,
      datum: strosek.datum ?? '',
      kategorijaId: strosek.kategorija ? strosek.kategorija.id : '',
    })
  }

  function preklici() {
    setUrejanjeId(null)
    setObrazec(prazenObrazec)
  }

  // DELETE
  async function izbrisi(id) {
    if (!window.confirm('Ali res želiš izbrisati ta strošek?')) return
    await fetch(`${API}/strosek/${id}`, { method: 'DELETE' })
    if (izbran && izbran.id === id) setIzbran(null)
    naloziPodatke()
  }

  // READ en: pridobi en strošek po ID-ju
  async function pokaziPodrobnosti(id) {
    const odgovor = await fetch(`${API}/strosek/${id}`)
    if (odgovor.ok) {
      setIzbran(await odgovor.json())
    }
  }

  // Dodaj novo kategorijo
  async function dodajKategorijo(e) {
    e.preventDefault()
    if (!novaKategorija.trim()) return
    await fetch(`${API}/kategorija`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ naziv: novaKategorija }),
    })
    setNovaKategorija('')
    naloziPodatke()
  }

  return (
    <div className="stran">
      <h1>Stroški</h1>

      {napaka && <p className="napaka">{napaka}</p>}

      <section className="kartica">
        <h2>{urejanjeId ? 'Uredi strošek' : 'Nov strošek'}</h2>
        <form onSubmit={shrani} className="obrazec">
          <input name="naziv" placeholder="Naziv" value={obrazec.naziv} onChange={spremeniPolje} required />
          <input name="znesek" type="number" step="0.01" placeholder="Znesek (€)" value={obrazec.znesek} onChange={spremeniPolje} required />
          <input name="datum" type="date" value={obrazec.datum} onChange={spremeniPolje} required />
          <select name="kategorijaId" value={obrazec.kategorijaId} onChange={spremeniPolje} required>
            <option value="">Izberi kategorijo</option>
            {kategorije.map((k) => (
              <option key={k.id} value={k.id}>{k.naziv}</option>
            ))}
          </select>
          <input name="opomba" placeholder="Opomba" value={obrazec.opomba} onChange={spremeniPolje} />
          <div className="gumbi">
            <button type="submit">{urejanjeId ? 'Shrani spremembe' : 'Dodaj'}</button>
            {urejanjeId && <button type="button" onClick={preklici}>Prekliči</button>}
          </div>
        </form>
      </section>

      <section className="kartica">
        <h2>Nova kategorija</h2>
        <form onSubmit={dodajKategorijo} className="obrazec vrstica">
          <input placeholder="Naziv kategorije" value={novaKategorija} onChange={(e) => setNovaKategorija(e.target.value)} />
          <button type="submit">Dodaj kategorijo</button>
        </form>
      </section>

      <section className="kartica">
        <h2>Vsi stroški</h2>
        <table>
          <thead>
            <tr>
              <th>Naziv</th>
              <th>Znesek</th>
              <th>Datum</th>
              <th>Kategorija</th>
              <th>Akcije</th>
            </tr>
          </thead>
          <tbody>
            {stroski.map((s) => (
              <tr key={s.id}>
                <td>{s.naziv}</td>
                <td>{Number(s.znesek).toFixed(2)} €</td>
                <td>{s.datum}</td>
                <td>{s.kategorija ? s.kategorija.naziv : '-'}</td>
                <td className="gumbi">
                  <button onClick={() => pokaziPodrobnosti(s.id)}>Podrobnosti</button>
                  <button onClick={() => zacniUrejanje(s)}>Uredi</button>
                  <button onClick={() => izbrisi(s.id)}>Izbriši</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {stroski.length === 0 && <p>Ni stroškov.</p>}
      </section>

      {izbran && (
        <section className="kartica">
          <h2>Podrobnosti stroška</h2>
          <p><strong>Naziv:</strong> {izbran.naziv}</p>
          <p><strong>Znesek:</strong> {Number(izbran.znesek).toFixed(2)} €</p>
          <p><strong>Datum:</strong> {izbran.datum}</p>
          <p><strong>Kategorija:</strong> {izbran.kategorija ? izbran.kategorija.naziv : '-'}</p>
          <p><strong>Opomba:</strong> {izbran.opomba || '-'}</p>
          <button onClick={() => setIzbran(null)}>Zapri</button>
        </section>
      )}
    </div>
  )
}

export default App