# Dettagli dell'Applicazione: "DiarioVisivo" (o "Momenti")

Per mantenere l'app semplice, minimale e performante in locale, ecco come dovrebbe essere strutturata tecnicamente e funzionalmente:

## Archiviazione 100% Locale:

- Le immagini vengono salvate nella memoria interna del telefono (nella cartella privata dell'app per non intasare la galleria generale).
- I testi e le date vengono salvati in un database leggero locale (es. SQLite / Room DB se sviluppi in nativo, o Isar / Hive se usi Flutter).

## Struttura del Dato (Cosa salva l'app):

- ID (univoco)
- Percorso_File_Immagine (stringa)
- Descrizione (testo libero)
- Data_Creazione (timestamp per ordinarle cronologicamente)

##Stile Grafico: Minimalista, stile iOS/Material You moderno. Sfondo bianco o scuro assoluto (Dark Mode), font puliti (es. Inter o Roboto), angoli arrotondati e ampi spazi vuoti per far "respirare" le foto.

# Struttura dei Mockup (Le Schermate)

L'applicazione avrà solo 3 schermate principali per garantire la massima semplicità.

## 1. Schermata Principale (Feed)

- In alto (Header): Titolo minimale a sinistra (es. I miei momenti) e un'icona a destra per le impostazioni (o info privacy).

- Corpo centrale (Il Feed): Una lista verticale a scorrimento (o una griglia a due colonne, stile Pinterest, molto elegante). Ogni elemento mostra:

1. La foto con angoli leggermente arrotondati.
2. Sotto la foto: La data in piccolo (es. 12 Maggio 2026) e la descrizione breve.
3. In basso a destra: Un grande bottone fluttuante (FAB) rotondo con un'icona + per aggiungere un nuovo ricordo.

## 2. Schermata "Aggiungi Ricordo"

- In alto: Una freccia per tornare indietro e il titolo Nuovo Ricordo.
- Centro:
  1. Un grande riquadro grigio con un'icona a forma di macchina fotografica. Cliccandoci si apre la galleria o la fotocamera dello smartphone.
  2. Una volta scelta la foto, il riquadro mostra l'anteprima dell'immagine.
  3. Sotto la foto: Un campo di testo minimal (senza bordi pesanti) con il placeholder "Scrivi un pensiero su questa esperienza...".
- In basso: Un bottone largo che occupa tutta la larghezza dello schermo con scritto Salva nei ricordi.

## 3. Schermata Dettaglio (Opzionale, al clic su una foto)

- La foto si apre a tutto schermo per essere rivissuta al meglio.
- In basso compare una tendina sfumata con la data completa e la descrizione estesa.
- Un piccolo tasto "Cestino" in un angolo per eliminare il ricordo (con conferma).

# Specifiche dell'applicazione:

## Tech Stack:

Android Nativo con Kotlin e Jetpack Compose. Per il database locale usa Room DB. Per il caricamento delle immagini usa Coil.

## Archiviazione Locale e Backup Semplificato:

Le immagini selezionate dalla galleria devono essere copiate e salvate nella cartella di archiviazione interna dell'applicazione per garantire che rimangano accessibili anche se l'utente le cancella dalla galleria principale. I dettagli (percorso immagine, descrizione, data) vanno salvati su Room DB.

**Backup e Ripristino:**
Per garantire che l'utente non perda i propri dati in caso di cambio telefono o reset, l'applicazione integra un sistema di Esportazione/Importazione manuale. Dalle impostazioni, l'utente può esportare un file compresso (.zip) contenente il database SQLite e le immagini locali. Questo file viene salvato tramite il selettore di file nativo di Android (Storage Access Framework), permettendo il salvataggio nella memoria interna, su SD, oppure direttamente su app Cloud (come Google Drive) se installate sul dispositivo. Successivamente, questo stesso file ZIP può essere importato per ripristinare interamente il diario visivo.

## UI/UX Design (Stile minimale, pulito, moderno ed elegante):

- Schermata 1 (Home Feed): Una lista verticale cronologica dei ricordi. Ogni post mostra la foto (angoli arrotondati), la data in piccolo e la descrizione del ricordo. In basso a destra c'è un Floating Action Button (FAB) con un'icona "+".
- Schermata 2 (Aggiungi Ricordo): Schermata pulita con un'area per selezionare un'immagine dalla galleria tramite PhotoPicker. Sotto la foto, un campo di testo (TextField) minimale per la descrizione. In fondo, un bottone "Salva" prominente ma elegante.
- Schermata 3 (Dettaglio): Cliccando su un post del feed, la foto si apre a schermo intero con la descrizione leggibile in basso e un'opzione per eliminare il post (che cancella sia il record nel DB sia il file dell'immagine locale).

# VINCOLI:

- non inventare nulla, se ci sono cose non specificate chiedi come procedere
- mantieni l'applicazione quanto più minimale possibile, per quanto riguarda ui/ux e numero di funzionalità
- l'app deve girare fluidamente su un android di fascia bassa, quindi ottimizza il codice
- mantieni il codice pulito, organizzato e commentato
- procedi sempre con un piano dettagliato e chiedi approvazione prima di implementare modifiche
