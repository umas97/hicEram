<div align="center">
  <img src="icon/icona.png" width="150" alt="Hic Eram Logo">
  <h1>Hic Eram</h1>
  <p><em>Un diario visivo e testuale per catturare e custodire i tuoi ricordi, offline e in totale sicurezza.</em></p>
</div>

---

**Hic Eram** è un'applicazione nativa per Android concepita come un archivio personale moderno. Progettata con un design pulito ispirato allo stile dei social network visuali, l'app ti permette di memorizzare i tuoi momenti speciali in modo ordinato, bello da vedere e, soprattutto, 100% privato.

## ✨ Funzionalità Principali

- 📸 **Feed Immersivo**: Sfoglia i tuoi ricordi come se fosse una moderna timeline. Ogni ricordo può contenere un titolo, una data, una descrizione dettagliata e fino a 10 foto da scorrere orizzontalmente in un comodo carosello.
- 🔎 **Visualizzazione Dettaglio & Pinch-to-Zoom**: Cliccando su un ricordo potrai leggere il testo per intero ed esplorare le immagini a tutto schermo con un naturalissimo gesto di "Pinch to Zoom".
- 🎨 **Estrema Personalizzazione (Temi Dinamici)**: Scegli tu l'aspetto del tuo diario! Dal comodo pannello "Impostazioni" puoi passare dal tema Chiaro a quello Scuro, oltre a scegliere uno dei 6 **Accenti Colore** (Blu, Verde, Rosso, Viola, Arancione, Rosa) che cambiano dinamicamente tutto il design dell'app in tempo reale.
- 🔒 **Privacy First (100% Offline)**: Nessun dato lascia il tuo dispositivo. Non ci sono server, nessun tracciamento in background. Le foto e i testi restano scritti nella memoria locale del tuo telefono, rendendo *Hic Eram* il posto più sicuro per i tuoi ricordi.
- 🗑️ **Gestione Avanzata & Cestino**: Metti "Mi Piace" (❤️) ai tuoi ricordi preferiti per filtrarli al volo, o usa la selezione multipla per spostare nel **Cestino** quelli meno importanti, da cui potrai decidere se ripristinarli o eliminarli in via definitiva.

## 📱 Come Scaricare e Installare l'App

Puoi scaricare l'applicazione pre-compilata e pronta all'uso direttamente dalla pagina delle **Release** di questo repository.

1. Vai alla sezione [**Releases**](../../releases/latest).
2. Sotto "Assets", scarica il file `app-debug.apk`.
3. Avvialo sul tuo smartphone Android e accetta l'installazione da origini sconosciute se richiesto.

## 🛠 Tecnologie Utilizzate

Questo progetto è interamente sviluppato utilizzando le tecnologie più moderne consigliate da Google per l'ecosistema Android:
- **Linguaggio**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (con design system Material 3)
- **Architettura**: MVVM (Model-View-ViewModel) con StateFlow per una reattività immediata
- **Persistenza Dati**: Libreria Room (per SQLite) e SharedPreferences
- **Elaborazione Immagini**: Libreria [Coil](https://coil-kt.github.io/coil/) per il caricamento asincrono e la gestione avanzata dell'UI zoomabile.
- **CI/CD**: Compilazione e rilascio automatizzati grazie a GitHub Actions.

---
<div align="center">
  <p><em>Realizzato con cuore ❤️ da <a href="http://samuelecastellan.altervista.org">Samuele Castellan</a></em></p>
</div>
