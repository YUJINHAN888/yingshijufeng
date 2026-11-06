#!/usr/bin/env python3
"""CineTrack companion: TMDb v3 connectivity smoke test.

Usage:
    python companion/tmdb_smoke.py --key YOUR_TMDB_V3_KEY
    TMDB_API_KEY=YOUR_KEY python companion/tmdb_smoke.py

The tool uses only the Python standard library. It is intentionally kept in the
same repository as the Android app so API-key/network problems can be diagnosed
without rebuilding the APK.
"""
import argparse
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE = "https://api.themoviedb.org/3"
TIMEOUT = 15


def fetch(path, api_key, params=None):
    query = {"api_key": api_key, "language": "zh-CN"}
    if params:
        query.update(params)
    url = BASE + path + "?" + urllib.parse.urlencode(query)
    request = urllib.request.Request(url, headers={"User-Agent": "CineTrack-Companion/0.2"})
    with urllib.request.urlopen(request, timeout=TIMEOUT) as response:
        return json.loads(response.read().decode("utf-8"))


def main():
    parser = argparse.ArgumentParser(description="CineTrack TMDb connectivity check")
    parser.add_argument("--key", default=os.environ.get("TMDB_API_KEY", ""), help="TMDb v3 API key")
    args = parser.parse_args()
    key = args.key.strip()
    if not key:
        print("missing API key: use --key or TMDB_API_KEY", file=sys.stderr)
        return 2

    try:
        config = fetch("/configuration", key)
        images = config.get("images", {}).get("secure_base_url", "")
        print("configuration: ok")
        print("image_base_url:", images or "(not returned)")
        data = fetch("/trending/movie/week", key)
        results = data.get("results", [])[:5]
        print("trending:", len(data.get("results", [])), "results")
        for movie in results:
            print(f"  {movie.get('title', '?')} ({str(movie.get('release_date', ''))[:4]})")
        return 0
    except urllib.error.HTTPError as error:
        print(f"HTTP {error.code}: {error.reason}", file=sys.stderr)
        return 1
    except Exception as error:
        print(f"network error: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
