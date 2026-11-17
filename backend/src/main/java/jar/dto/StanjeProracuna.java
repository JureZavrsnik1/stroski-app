package jar.dto;

public enum StanjeProracuna {
    V_REDU,          // poraba pod 80 % proračuna
    OPOZORILO,       // poraba od 80 % do vključno 100 %
    PREKORACEN,      // poraba nad 100 %
    BREZ_PRORACUNA   // kategorija nima proračuna
}
