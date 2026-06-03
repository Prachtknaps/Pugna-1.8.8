# Pugna

**Pugna** ist ein Minecraft-Spigot-Plugin für Version **1.8.8**, welches an das legendäre YouTuber-Projekt **VARO** angelehnt ist.

Das Plugin verwaltet eine eigene Lobby, Teams, Countdowns, Spielphasen, Regeln, Teleports, Nether-/Border-Events und den Ablauf einer Pugna-Runde.

## Voraussetzungen

- Minecraft-Server mit **Spigot 1.8.8**
- Java-kompatible Serverumgebung für Spigot 1.8.8
- Zugriff auf die Serverdateien
- Das kompilierte Pugna-Plugin im `plugins`-Ordner

## Installation

1. Server stoppen.
2. Die Plugin-Datei in den Ordner `plugins` legen.
3. Die unten beschriebenen Serverdateien anpassen.
4. Die Lobby-Welt vorbereiten.
5. Server starten.

## Wichtige Server-Vorbereitung

Vor dem ersten Start mit Pugna müssen die Standardwelten entfernt und die Lobby-Welt eingefügt werden.

### Welten löschen

Folgende Ordner im Server-Hauptverzeichnis löschen:

```text
world
world_nether
world_end
```

### Lobby-Welt einfügen

Die Welt

```text
lobby
```

aus dem Repository

```text
Pugna-1.8.8
```

in den Server-Hauptordner kopieren.

Danach sollte die Ordnerstruktur ungefähr so aussehen:

```text
server/
├── lobby/
├── plugins/
├── bukkit.yml
├── server.properties
└── spigot.jar
```

## Server-Konfiguration

Damit Pugna korrekt funktioniert, müssen einige Standardwerte des Servers angepasst werden.

### bukkit.yml

Folgende Werte setzen:

```yaml
allow-end: false
connection-throttle: 0
```

Falls die Datei vorher diese Werte enthält:

```yaml
allow-end: true
connection-throttle: 4000
```

müssen sie entsprechend ersetzt werden.

### server.properties

Folgende Werte setzen:

```properties
spawn-protection=0
allow-nether=false
difficulty=3
announce-player-achievements=false
allow-flight=true
```

Falls die Datei vorher diese Werte enthält:

```properties
spawn-protection=16
allow-nether=true
difficulty=1
announce-player-achievements=true
allow-flight=false
```

müssen sie entsprechend ersetzt werden.

## Plugin-Konfiguration

Nach dem ersten Serverstart erstellt Pugna seine Konfigurationsdateien im Plugin-Ordner.

Die Dateien sind:

```text
plugins/Pugna/config.yml
plugins/Pugna/game.yml
plugins/Pugna/messages.yml
```

### config.yml

In der `config.yml` werden unter anderem folgende Dinge konfiguriert:

- Entwicklungsmodus
- Spielzeitfenster
- Teamgrößen
- minimale und maximale Spieleranzahl
- Lobby-Welt und Lobby-Spawn
- Pugna-Welt
- Border-Größen
- Countdown-Zeiten
- Event-Zeitpunkte
- Item-Namen

Wichtige Lobby-Einstellungen:

```yaml
lobby:
  world: lobby
  spawn:
    x: 0.5
    y: 64.0
    z: 0.5
    yaw: 0.0
    pitch: 0.0
```

## Befehle

### /pugna

Zeigt alle verfügbaren Pugna-Befehle an.

Nur ausführbar durch:

- Konsole
- OP-Spieler

Normale Spieler haben keine Berechtigung.

```text
/pugna
```

### /countdown

Setzt einen aktiven Countdown.

```text
/countdown set <seconds>
```

Beispiel:

```text
/countdown set 30
```

### /gui

Aktiviert oder deaktiviert die GUI für den Spieler.

```text
/gui enable
/gui disable
```

### /hub

Verlässt den Server beziehungsweise kickt den Spieler zurück zur Hub-Logik.

```text
/hub
```

### /rules

Zeigt die Pugna-Regeln an.

```text
/rules items
/rules enchantments
/rules potions
```

### /team

Verwaltet Teams.

```text
/team list
/team join <name>
/team rename <name>
/team leave
```

## Spielablauf

Der grobe Ablauf ist:

1. Spieler betreten die Lobby.
2. Spieler werden Teams zugewiesen oder treten Teams bei.
3. Der Lobby-Countdown startet, sobald genug Spieler online sind.
4. Spieler werden in die Pugna-Welt teleportiert.
5. Das Spiel startet nach dem Game-Countdown.
6. Während des Spiels laufen verschiedene Events:
    - Nether-Freischaltung
    - Border-Shrink
    - Nether-Schließung
    - Enemy-Reveal
    - Spielende
7. Am Ende gewinnt der letzte verbleibende Spieler oder das letzte verbleibende Team.

## Hinweise für den Betrieb

- Der Server sollte vor Änderungen an Welten oder Konfigurationsdateien immer gestoppt werden.
- Die Ordner `world`, `world_nether` und `world_end` sollten vor dem ersten Pugna-Start gelöscht werden.
- Die Lobby-Welt muss im Server-Hauptordner liegen und `lobby` heißen, sofern in der Config kein anderer Name eingestellt ist.
- `allow-flight=true` ist wichtig, damit Spieler nicht fälschlicherweise wegen Flugbewegungen gekickt werden.
- `connection-throttle=0` verhindert Probleme bei schnellen Reconnects während Tests oder Spielphasen.
- `spawn-protection=0` verhindert, dass der Server eigene Spawn-Schutzregeln über die Pugna-Logik legt.

## Entwicklung

Für die Entwicklung kann in der `config.yml` der Entwicklungsmodus aktiviert werden.

```yaml
game:
  development: true
```

Im Entwicklungsmodus kann das Verhalten vom normalen Spielbetrieb abweichen, zum Beispiel beim Erstellen oder Zurücksetzen von Welten.

## Lizenz

Dieses Projekt ist für den privaten Gebrauch im Rahmen von Pugna vorgesehen.
