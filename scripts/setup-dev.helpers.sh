#!/usr/bin/env bash
# Shared helpers for Helical Insight local development setup.

patch_persistence_placeholders() {
  local file="$1"
  [ -f "$file" ] || return 1

  local tmp="${file}.tmp"
  sed -E \
    -e 's|(<property name="hibernate\.dialect" value=")[^$][^"]*(")|\1${dbDialect}\2|g' \
    -e 's|(<property name="jakarta\.persistence\.jdbc\.driver" value=")[^$][^"]*(")|\1${dbDriver}\2|g' \
    -e 's|(<property name="jakarta\.persistence\.jdbc\.url" value=")[^$][^"]*(")|\1${dbUrl}\2|g' \
    -e 's|(<property name="jakarta\.persistence\.jdbc\.user" value=")[^$][^"]*(")|\1${dbUser}\2|g' \
    -e 's|(<property name="jakarta\.persistence\.jdbc\.password" value=")[^$][^"]*(")|\1${dbPassword}\2|g' \
    -e 's|__HI_DB_PATH__|\${dbBasePath}|g' \
    "$file" > "$tmp"

  if ! cmp -s "$file" "$tmp"; then
    mv "$tmp" "$file"
    return 0
  fi

  rm -f "$tmp"
  return 1
}

collect_persistence_xml_files() {
  local server_root="$1"

  find "$server_root/presentation/src" -path "*/META-INF/persistence.xml" -type f 2>/dev/null

  if [ -d "$server_root/presentation/target" ]; then
    find "$server_root/presentation/target" -path "*/META-INF/persistence.xml" -type f 2>/dev/null
  fi
}