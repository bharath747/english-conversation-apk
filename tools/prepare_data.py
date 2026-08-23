#!/usr/bin/env python3
"""Port the exact Learn and Games data from english-conversation-app into Android assets."""
from pathlib import Path
from urllib.request import urlopen
import re

BASE = "https://raw.githubusercontent.com/bharath747/english-conversation-app/main/"
FILES = ["learn-data.js", "learn-extra.js", "game-data.js"]
out = Path("app/src/main/assets")
out.mkdir(parents=True, exist_ok=True)
raw = {}
for name in FILES:
    with urlopen(BASE + name, timeout=30) as r:
        raw[name] = r.read().decode("utf-8")

# Each lesson is exactly five single-quoted fields in the source PWA.
lesson_re = re.compile(r"\['([^']*)','([^']*)','([^']*)','([^']*)','([^']*)'\]")
lessons = lesson_re.findall(raw["learn-data.js"] + "\n" + raw["learn-extra.js"])
if len(lessons) < 200:
    raise SystemExit(f"Expected the full lesson set; found only {len(lessons)} lessons")
with (out / "lessons.tsv").open("w", encoding="utf-8", newline="\n") as f:
    for row in lessons:
        f.write("\t".join(row) + "\n")

words_match = re.search(r"UKG_GAME_WORDS=\[(.*?)\];window\.UKG_GAME_EMOJI", raw["game-data.js"], re.S)
if not words_match:
    raise SystemExit("Could not read UKG_GAME_WORDS")
words = re.findall(r"'([^']+)'", words_match.group(1))
# Preserve JavaScript object behavior: duplicate keys use the final value.
pairs = re.findall(r"([A-Za-z]+):'([^']+)'", raw["game-data.js"])
emojis = {}
for key, value in pairs:
    emojis[key] = value
if len(words) < 100 or len(emojis) < 100:
    raise SystemExit(f"Incomplete game data: {len(words)} words, {len(emojis)} emoji entries")
with (out / "games.tsv").open("w", encoding="utf-8", newline="\n") as f:
    for word in words:
        f.write(word + "\t" + emojis.get(word, "❓") + "\n")

print(f"Prepared {len(lessons)} lessons and {len(words)} game words")
